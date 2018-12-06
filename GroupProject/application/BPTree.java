package application;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.TreeMap;

/**
 * 
 * Implementation of a B+ tree to allow efficient access to
 * many different indexes of a large data set. 
 * BPTree objects are created for each type of index
 * needed by the program.  BPTrees provide an efficient
 * range search as compared to other types of data structures
 * due to the ability to perform log_m N lookups and
 * linear in-order traversals of the data items.
 * 
 * @author sapan (sapan@cs.wisc.edu), Soua Lor, Maria Helgeson, Daniel Walter, & Will Hickman
 *
 * @param <K> key - expect a string that is the type of id for each item
 * @param <V> value - expect a user-defined type that stores all data for a food item
 */
public class BPTree<K extends Comparable<K>, V> implements BPTreeADT<K, V> {
	
	// to distinguish internal vs. leaf for splitting operations
	enum NodeType
	{
		Internal,
		Leaf
	}
	
    // Root of the tree
    private Node root;
    
    // Branching factor is the number of children nodes 
    // for internal nodes of the tree
    private int branchingFactor;
    
    // for determining size of sibling during split
    // if branchingFactor is even, then siblingSize = branchingFactor / 2
    // if branchingFactor is odd, then siblingSize = (branchingFactor + 1) / 2 
    private int siblingSize;
    
    
    /**
     * Public constructor
     * 
     * @param branchingFactor 
     */
    public BPTree(int branchingFactor) {
        if (branchingFactor <= 2) {
            throw new IllegalArgumentException(
               "Illegal branching factor: " + branchingFactor);
        }
        this.branchingFactor = branchingFactor;
        this.siblingSize = (branchingFactor + 1) / 2;	// integer division, so remainder gets dropped
        
        this.root = new LeafNode();
    }
    
    
    /**
     * Inserts the key and value in the appropriate nodes in the tree
     * 
     * Note: key-value pairs with duplicate keys can be inserted into the tree.
     * 
     * @param key
     * @param value
     */
    @Override
    public void insert(K key, V value) {
    	
    	// special case of the root being a leafNode, in which case we need to update the root to be an internal node
    	this.root.insert(key, value);
    	if (this.root.shouldSplit())
    	{
    		InternalNode newRoot = new InternalNode();
    		Node oldRoot = this.root;
    		Node newSibling = this.root.split();
    		newRoot.leastChild = newSibling;
    		
    		if (oldRoot.type == NodeType.Internal)
    		{
    			@SuppressWarnings("unchecked")
				InternalNode asInternal = (InternalNode) oldRoot;
    			Entry<K, Node> firstEntry = asInternal.childMap.firstEntry();
    			Node newLeastChild = firstEntry.getValue();
    			asInternal.childMap.remove(firstEntry.getKey());
    			asInternal.leastChild = newLeastChild;
    			
    		}
    		else
    		{
    			newRoot.childMap.put(oldRoot.getFirstKey(), oldRoot);
    		}
    		
    		K middleKey = oldRoot.getFirstKey();
			// if child that split was our least child, then we need to make a new least child, etc.
			newRoot.leastChild = newSibling;
			newRoot.childMap.put(middleKey, oldRoot);
			
    		this.root = newRoot;
    	}
    }
    
    /**
     * Gets the values that satisfy the given range 
     * search arguments.
     * 
     * Value of comparator can be one of these: 
     * "<=", "==", ">="
     * 
     * Example:
     *     If given key = 2.5 and comparator = ">=":
     *         return all the values with the corresponding 
     *      keys >= 2.5
     *      
     * If key is null or not found, return empty list.
     * If comparator is null, empty, or not according
     * to required form, return empty list.
     * 
     * @param key to be searched
     * @param comparator is a string
     * @return list of values that are the result of the 
     * range search; if nothing found, return empty list
     */
    @Override
    public List<V> rangeSearch(K key, String comparator) {
    	if (key == null || comparator == null)
        {
        	return new ArrayList<V>();
        }
    	if (!comparator.contentEquals(">=") && 
            !comparator.contentEquals("==") && 
            !comparator.contentEquals("<=") )
        {
        	return new ArrayList<V>();
        }
        
    	return this.root.rangeSearch(key, comparator);
    }
    
 // temporary print tag for testing the LeafNode links for in-order printing
    private String inOrderPrint()
    {
    	List<V> rtnList = this.rangeSearch(this.root.getFirstLeafKey(), ">=");
    	
    	StringBuilder sb = new StringBuilder();
    	
    	for (V val: rtnList)
    	{
    		sb.append(val.toString());
    		sb.append(", ");
    	}
    	
    	return sb.toString();
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("unchecked")
	@Override
    public String toString() {
        Queue<List<Node>> queue = new LinkedList<List<Node>>();
        queue.add(Arrays.asList(root));
        StringBuilder sb = new StringBuilder();
        while (!queue.isEmpty()) {
            Queue<List<Node>> nextQueue = new LinkedList<List<Node>>();
            while (!queue.isEmpty()) {
                List<Node> nodes = queue.remove();
                sb.append('{');
                Iterator<Node> it = nodes.iterator();
                while (it.hasNext()) {
                    Node node = it.next();
                    sb.append(node.toString());
                    if (it.hasNext())
                        sb.append(", ");
                    if (node instanceof BPTree.InternalNode)
                    {
                    	InternalNode asInt = (InternalNode) node;
                    	nextQueue.add(Arrays.asList(asInt.leastChild));
                    	for (Node nxt: asInt.childMap.values())
                    	{
                    		nextQueue.add(Arrays.asList(nxt));
                    	}
                    }
                        
                }
                sb.append('}');
                if (!queue.isEmpty())
                    sb.append(", ");
                else {
                    sb.append('\n');
                }
            }
            queue = nextQueue;
        }
        return sb.toString();
    }
    
    
    /**
     * This abstract class represents any type of node in the tree
     * This class is a super class of the LeafNode and InternalNode types.
     * 
     * @author sapan
     */
    private abstract class Node {
        
        NodeType type;
        
        /**
         * Package constructor
         */
        Node() {
            
        }
        
        abstract Collection<K> getKeyColl();
        
        /**
         * Inserts key and value in the appropriate leaf node 
         * and balances the tree if required by splitting
         *  
         * @param key
         * @param value
         */
        abstract void insert(K key, V value);

        /**
         * Gets the first leaf key of the tree
         * 
         * @return key
         */
        abstract K getFirstLeafKey();
        
        abstract K getFirstKey();
        
        /**
         * Gets the new sibling created after splitting the node
         * 
         * @return Node
         */
        abstract Node split();
        
        /*
         * (non-Javadoc)
         * @see BPTree#rangeSearch(java.lang.Object, java.lang.String)
         */
        abstract List<V> rangeSearch(K key, String comparator);
        
        /**
         * tell parent whether you need to split
         * Will be different criteria for leaf vs. internal, since internal 
         * can have branchingFactor - 1 number of keys, but leaf can have 
         * branchingFactor number of keys
         * @return
         */
        abstract boolean shouldSplit();
        
        public String toString() {
            return getKeyColl().toString();
        }
    
    } // End of abstract class Node
    
    /**
     * This class represents an internal node of the tree.
     * This class is a concrete sub class of the abstract Node class
     * and provides implementation of the operations
     * required for internal (non-leaf) nodes.
     * 
     * @author sapan
     */
    private class InternalNode extends Node {

        TreeMap<K, Node> childMap;
        
        Node leastChild;
        
        /**
         * Package constructor
         */
        InternalNode() {
            super();
            this.childMap = new TreeMap<K, Node>();
            this.type = NodeType.Internal;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#getFirstLeafKey()
         */
        K getFirstLeafKey() {
            return this.leastChild.getFirstLeafKey();
        }
        
        K getFirstKey() {
        	return this.childMap.firstKey();
        }
        
        /**
         * List of keys for this node
         * @return LinkedList of keys for this node
         */
        Collection<K> getKeyColl() {
            return this.childMap.keySet();
        }
        
        boolean shouldSplit()
        {
        	if (this.childMap.keySet().size() + 1 > branchingFactor)
        	{
        		return true;
        	}
        	else
        	{
        		return false;
        	}
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#insert(java.lang.Comparable, java.lang.Object)
         */
        void insert(K key, V value) {
        	
        	K nxtKey = this.childMap.firstKey();
        	K lastKey = null;
        	
        	Node childToUpdate = null;
        	
        	if (key.compareTo(nxtKey) < 0)
        	{
        		childToUpdate = this.leastChild;
        	}
        	else
        	{
        		lastKey = nxtKey;
        		nxtKey = this.childMap.higherKey(nxtKey);
        		while (nxtKey != null)
            	{
        			if (key.compareTo(nxtKey) < 0)
            		{
            			childToUpdate = this.childMap.get(lastKey);
            			break;
            		}
        			else
        			{
        				lastKey = nxtKey;
        				nxtKey = this.childMap.higherKey(nxtKey);
        			}
            	}
        		if (childToUpdate == null)
        		{
        			childToUpdate = this.childMap.lastEntry().getValue();
        		}
        	}
        	
        	childToUpdate.insert(key, value);
			if (childToUpdate.shouldSplit())  // should be == ?
			{
				Node sibling = childToUpdate.split();
				
				if (childToUpdate.type == NodeType.Internal)
				{
					@SuppressWarnings("unchecked")
					InternalNode asInternal = (InternalNode)childToUpdate;
					// if it's an index node, then it has one more key than it needs, now that we've split it, so remove it and add it to our list
					Entry<K, Node> firstEntry = asInternal.childMap.firstEntry();
					Node newLeastChild = firstEntry.getValue();
					asInternal.childMap.remove(firstEntry.getKey());
					asInternal.leastChild = newLeastChild;
				}
				else
				{
					this.childMap.put(childToUpdate.getFirstKey(), childToUpdate);
				}
				K middleKey = childToUpdate.getFirstKey();
				// if child that split was our least child, then we need to make a new least child, etc.
				if (lastKey == null)
				{
					this.leastChild = sibling;
					this.childMap.put(middleKey, childToUpdate);
				}
				// otherwise, that child's already in our map, but the sibling goes at the key the original child was at, and we'll add a new key for the original child
				else
				{
					this.childMap.put(lastKey, sibling);
				}
			}
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#split()
         */
        Node split() {
        	InternalNode newSibling = new InternalNode();
        	newSibling.leastChild = this.leastChild;
            for (int i = 0; i < siblingSize - 1; i++)
            {
            	K nxtKey = this.childMap.firstKey();	// linked list, so this is fast
            	Node nxtChild = this.childMap.remove(nxtKey);
            	newSibling.childMap.put(nxtKey, nxtChild);
            }
            
            return newSibling;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#rangeSearch(java.lang.Comparable, java.lang.String)
         */
        List<V> rangeSearch(K key, String comparator) {
        	// if less than, then we just need to get to the beginning of the list. 
        	if (comparator.contentEquals("<="))
        	{
    			Node child = this.leastChild;
    			return child.rangeSearch(key, comparator);
        	}
        	// if >= or ==, we need to go find a leaf with that key (or maybe one that's just smaller or just bigger, if it's not in the list)
        	else
        	{
        		K nxtKey = this.childMap.firstKey();
        		
        		if (key.compareTo(nxtKey) <= 0)
        		{
        			return this.leastChild.rangeSearch(key, comparator);
        		}
        		
        		/*
        		lastKey = nxtKey;
        		nxtKey = this.childMap.higherKey(nxtKey);
        		while (nxtKey != null)
            	{
        			if (key.compareTo(nxtKey) < 0)
            		{
            			childToUpdate = this.childMap.get(lastKey);
            			break;
            		}
        			else
        			{
        				lastKey = nxtKey;
        				nxtKey = this.childMap.higherKey(nxtKey);
        			}
            	}
        		if (childToUpdate == null)
        		{
        			childToUpdate = this.childMap.lastEntry().getValue();
        		}
        		 */
        		K lastKey = nxtKey;
        		while (nxtKey != null)
            	{
        			if (key.compareTo(nxtKey) > 0)
        			{
        				lastKey = nxtKey;
        				nxtKey = this.childMap.higherKey(nxtKey);
        			}
            	}
        		return this.childMap.get(lastKey).rangeSearch(key, comparator);
        	}
        }
    
    } // End of class InternalNode
    
    
    /**
     * This class represents a leaf node of the tree.
     * This class is a concrete sub class of the abstract Node class
     * and provides implementation of the operations that
     * required for leaf nodes.
     * 
     * @author sapan
     */
    private class LeafNode extends Node {
        
        // key based tree that maps to list of all records that match that key
    	TreeMap<K, List<V>> valueMap;
        
        // Reference to the next leaf node
        LeafNode next;
        
        // Reference to the previous leaf node
        LeafNode previous;
        
        /**
         * Package constructor
         */
        LeafNode() {
            super();
            this.valueMap = new TreeMap<K, List<V>>();
            this.type = NodeType.Leaf;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#getFirstLeafKey()
         */
        K getFirstLeafKey() {
            return this.valueMap.firstKey();
        }
        
        K getFirstKey() {
        	return this.valueMap.firstKey();
        }
        
        Collection<K> getKeyColl() {
        	return this.valueMap.keySet();
        }
        
        boolean shouldSplit()
        {
        	if (this.valueMap.keySet().size() > branchingFactor)
        	{
        		return true;
        	}
        	else
        	{
        		return false;
        	}
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#insert(Comparable, Object)
         */
        void insert(K key, V value) {
    		
        	if (!this.valueMap.containsKey(key))
        	{
        		List<V> newList = new LinkedList<V>();
        		newList.add(value);
        		this.valueMap.put(key, newList);
        	}
        	else
        	{
        		this.valueMap.get(key).add(value);
        	}
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#split()
         */
        Node split() {
        	
        	LeafNode newSibling = new LeafNode();
        	int taken = 0;
            while (taken < siblingSize)
            {
            	K nxtKey = this.valueMap.firstKey();
            	List<V> nxtValList = this.valueMap.remove(nxtKey);
            	newSibling.valueMap.put(nxtKey, nxtValList);
            	taken++;
            }
            newSibling.next = this;
            LeafNode previous = this.previous;
            newSibling.previous = previous;
            if (previous != null)
            {
            	previous.next = newSibling;
            }
            this.previous = newSibling;
            
            return newSibling;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#rangeSearch(Comparable, String)
         */
        List<V> rangeSearch(K key, String comparator) {
        	
        	List<V> rtnList = new LinkedList<V>(); // use linked list because adding should be fast
        	// if less than, then start at the beginning of our list
        	if (comparator.contentEquals("<="))
        	{
        		
        		// if we have an internal node parent, then this check has already been performed for us, but not if the root is a leaf
        		if (key.compareTo(this.getFirstLeafKey()) >= 0) // make sure we have something to return
        		{
        			boolean contToNxt = true;
            		for (K nxtKey: this.valueMap.keySet())
            		{
            			if (nxtKey.compareTo(key) <= 0)
            			{
            				rtnList.addAll(this.valueMap.get(nxtKey));
            			}
            			else
            			{
            				contToNxt = false;
            				break;
            			}
            		}
            		if (contToNxt && (this.next != null))
            		{
            			rtnList.addAll(this.next.rangeSearch(key, comparator));
            		}
        		}
        	}
        	// if ==, go find it!
        	else if (comparator.contentEquals("=="))
        	{
        		List<V> list = this.valueMap.get(key);
        		if (list != null)
        		{
        			rtnList.addAll(list);
        		}
        	}
        	// greater than or equal to
        	else
        	{
        		
        		K nxtKey = this.valueMap.ceilingKey(key);
        		
        		while (nxtKey != null)
        		{
        			rtnList.addAll(this.valueMap.get(nxtKey));
        			nxtKey = this.valueMap.higherKey(nxtKey);
        		}
        		
        		if (this.next != null)
        		{
        			rtnList.addAll(this.next.rangeSearch(key, comparator));
        		}
        	}
        	return rtnList;
        }
        
    } // End of class LeafNode
    
    /**
     * Contains a basic test scenario for a BPTree instance.
     * It shows a simple example of the use of this class
     * and its related types.
     * 
     * @param args
     */
    public static void main(String[] args) {
        // create empty BPTree with branching factor of 3
        BPTree<Double, Double> bpTree = new BPTree<>(6);

        // create a pseudo random number generator
        Double[] inputs = new Double[] { 91d, 91d, 10d, 14d, 91d, 89d, 15d, 23d, 91d, 25d, 89d, 13d, 91d, 47d, 89d, 91d, 5d, 53d, 91d, 89d, 17d, 21d, 91d, 89d, 63d, 89d, 91d, 55d, 91d, 79d, 91d, 57d, 5d, 5d, 5d, 91d, 5d, 5d, 91d, 5d, 5d, 5d, 91d, 5d, 5d };
        Double[] inOrder = new Double[] { 5d, 5d, 5d, 5d, 5d, 5d, 5d, 5d, 5d, 5d, 5d, 10d, 13d, 14d, 15d, 17d, 21d, 23d, 25d, 47d, 53d, 55d, 57d, 63d, 79d, 89d, 89d, 89d, 89d, 89d, 89d, 91d, 91d, 91d, 91d, 91d, 91d, 91d, 91d, 91d, 91d, 91d, 91d, 91d, 91d };

        // build an ArrayList of those value and add to BPTree also
        // allows for comparing the contents of the ArrayList 
        // against the contents and functionality of the BPTree
        // does not ensure BPTree is implemented correctly
        // just that it functions as a data structure with
        // insert, rangeSearch, and toString() working.
        for (int i = 0; i < inputs.length; i++) {
        	//System.out.println();
        	//System.out.println("Inserting " + inputs[i]);
        	//System.out.println();
            bpTree.insert(inputs[i], inputs[i]);
            //System.out.println("\n\nTree structure:\n" + bpTree.toString());
            //System.out.println("Tree sorted:   " + bpTree.inOrderPrint());
        }
        StringBuilder sb = new StringBuilder();
        //System.out.print("Correct order: "); 
        for (Double d: inOrder)
        {
        	sb.append(d);
        	sb.append(", ");
        }
        String expected = sb.toString();
        
        System.out.println(bpTree.toString());
        
        String actual = bpTree.inOrderPrint();
        
        if (!actual.contentEquals(expected))
        {
        	System.out.println("In order traversal returned unexpected results");
        	System.out.println("Expected: " + expected);
        	System.out.println("Actual: " + actual);
        }
        else
        {
        	System.out.println("All tests passed!");
        }
        //System.out.println();
        //List<Double> filteredValues = bpTree.rangeSearch(63d, ">=");
        //System.out.println("rangeSearch(63,\"<=\"): " + filteredValues.toString());
    }

} // End of class BPTree

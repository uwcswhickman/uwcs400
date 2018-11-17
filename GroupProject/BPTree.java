import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Implementation of a B+ tree to allow efficient access to
 * many different indexes of a large data set. 
 * BPTree objects are created for each type of index
 * needed by the program.  BPTrees provide an efficient
 * range search as compared to other types of data structures
 * due to the ability to perform log_m N lookups and
 * linear in-order traversals of the data items.
 * 
 * @author sapan (sapan@cs.wisc.edu)
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
    
    // temporary print tag for testing the LeafNode links for in-order printing
    private String inOrderPrint()
    {
    	StringBuilder sb = new StringBuilder();
    	
    	Node current = this.root;
    	
    	boolean leafFound = false;
    	while (!leafFound)
    	{
    		if (current.type == NodeType.Leaf)
    		{
    			leafFound = true;
    		}
    		else
    		{
    			@SuppressWarnings("unchecked")
				InternalNode asInternal = (InternalNode)current;
    			current = asInternal.children.get(0);
    		}
    	}
    	
    	@SuppressWarnings("unchecked")
		LeafNode asLeaf = (LeafNode)current;
    	
    	while (asLeaf != null)
    	{
    		for (V val: asLeaf.values)
        	{
        		sb.append(val.toString() + ", ");
        	}
    		asLeaf = asLeaf.next;
    	}
    	
    	
    	return sb.toString();
    }
    
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
    		Node newSibling = this.root.split();
    		Node oldRoot = this.root;
    		InternalNode newRoot = new InternalNode();
    		newRoot.children.add(newSibling);
    		newRoot.children.add(oldRoot);
    		if (this.root.type == NodeType.Internal)
    		{
    			K middleKey = oldRoot.keys.remove(0);
    			newRoot.keys.add(middleKey);
    		}
    		else
    		{
    			newRoot.keys.add(oldRoot.getFirstLeafKey());
    		}
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
        if (!comparator.contentEquals(">=") && 
            !comparator.contentEquals("==") && 
            !comparator.contentEquals("<=") )
        {
        	return new ArrayList<V>();
        }
        if (key == null)
        {
        	return new ArrayList<V>();
        }
        return null;
    }
    
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
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
                        nextQueue.add(((InternalNode) node).children);
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
        
    	// List of keys
        List<K> keys;
        
        NodeType type;
        
        /**
         * Package constructor
         */
        Node() {
            this.keys = new LinkedList<K>();
        }
        
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
         * 
         * @return boolean
         */
        abstract boolean isOverflow();
        
        /**
         * tell parent whether you need to split
         * Will be different criteria for leaf vs. internal, since internal 
         * can have branchingFactor - 1 number of keys, but leaf can have 
         * branchingFactor number of keys
         * @return
         */
        abstract boolean shouldSplit();
        
        public String toString() {
            return keys.toString();
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

        // List of children nodes
        List<Node> children;
        
        /**
         * Package constructor
         */
        InternalNode() {
            super();
            this.children = new LinkedList<Node>();
            this.type = NodeType.Internal;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#getFirstLeafKey()
         */
        K getFirstLeafKey() {
            return this.children.get(0).getFirstLeafKey();
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#isOverflow()
         */
        boolean isOverflow() {
            // TODO : Complete
            return false;
        }
        
        boolean shouldSplit()
        {
        	if (this.children.size() > branchingFactor)
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
        	int i = 0;
        	// children will be in order, so this iterator will be in order
        	for (K nxtKey: this.keys)
        	{
        		if (key.compareTo(nxtKey) < 0)
        		{
        			break;
        		}
        		else
        		{
        			i++;
        		}
        	}
        	
        	Node child = this.children.get(i);
			child.insert(key, value);
			if (child.shouldSplit())  // should be == ?
			{
				Node sibling = child.split();
				this.children.add(i, sibling);
				if (child.type == NodeType.Internal)
				{
					K middleKey = child.keys.remove(0);
					this.keys.add(i, middleKey);
				}
				else
				{
					this.keys.add(i, child.getFirstLeafKey());
				}
			}
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#split()
         */
        Node split() {
        	InternalNode newSibling = new InternalNode();
            for (int i = 0; i < siblingSize - 1; i++)
            {
            	K nxtKey = this.keys.remove(0);	// linked list, so this is fast
            	Node nxtChild = this.children.remove(0);
            	newSibling.keys.add(nxtKey);  // linked list, so this goes at the end, and it will be in order too because of the way we're looping on the current node
            	newSibling.children.add(nxtChild);
            }
            // there's one more child than there is key, and we took x number of keys, so we need to take x+1 children
            Node middleChild = this.children.remove(0);
            newSibling.children.add(middleChild);
            
            return newSibling;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#rangeSearch(java.lang.Comparable, java.lang.String)
         */
        List<V> rangeSearch(K key, String comparator) {
            // TODO : Complete
            return null;
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
        
        // List of values
        List<V> values;
        
        // Reference to the next leaf node
        LeafNode next;
        
        // Reference to the previous leaf node
        LeafNode previous;
        
        /**
         * Package constructor
         */
        LeafNode() {
            super();
            this.values = new LinkedList<V>();
            this.type = NodeType.Leaf;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#getFirstLeafKey()
         */
        K getFirstLeafKey() {
            return this.keys.get(0);
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#isOverflow()
         */
        boolean isOverflow() {
            // TODO : Complete
            return false;
        }
        
        boolean shouldSplit()
        {
        	if (this.keys.size() > branchingFactor)
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
    		int i = 0; 
    		boolean duplicate = false;
    		for (K nxt: this.keys)
    		{
    			int compareTo = key.compareTo(nxt);
    			if (compareTo > 0)
    			{
    				i++;
    			}
    			else if (compareTo == 0)
    			{
    				duplicate = true;
    				break;
    			}
    			else
    			{
    				break;
    			}
    		}
    		if (duplicate)
    		{
    			this.values.set(i, value);
    		}
    		else
    		{
    			// this is dumb, because we're retracing our steps here; would be better to implement our own custom in-order linked list, but maybe i'll come back to that. 
    			this.keys.add(i, key);
    			this.values.add(i, value);
    		}
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#split()
         */
        Node split() {
        	// debug System.out.println("Before: this = " + valStr(this) + ": this.previous = " + valStr(this.previous) + ", this.next = " + valStr(this.next));
        	LeafNode newSibling = new LeafNode();
            for (int i = 0; i < siblingSize; i++)
            {
            	K nxtKey = this.keys.remove(0);	// linked list, so this is fast
            	V nxtValue = this.values.remove(0);
            	newSibling.keys.add(nxtKey);  // linked list, so this goes at the end, and it will be in order too because of the way we're looping on the current node
            	newSibling.values.add(nxtValue);
            }
            newSibling.next = this;
            LeafNode previous = this.previous;
            newSibling.previous = previous;
            if (previous != null)
            {
            	previous.next = newSibling;
            }
            this.previous = newSibling;
            // debug System.out.println("After: this = " + valStr(this) + ": this.previous = " + valStr(this.previous) + ", this.next = " + valStr(this.next));
            // debug System.out.println("After: newSibling = " + valStr(newSibling) + ": newSibling.previous = " + valStr(newSibling.previous) + ", newSibling.next = " + valStr(newSibling.next));
            
            return newSibling;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#rangeSearch(Comparable, String)
         */
        List<V> rangeSearch(K key, String comparator) {
            // TODO : Complete
            return null;
        }
        String valStr(LeafNode n)
        {
        	if (n == null)
        	{
        		return "null";
        	}
        	else 
        	{
        		StringBuilder sb = new StringBuilder();
            	for (V val: n.values)
            	{
            		sb.append(val + ", ");
            	}
            	return sb.toString();
        	}
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
        BPTree<Double, Double> bpTree = new BPTree<>(5);

        // create a pseudo random number generator
        Double[] inputs = new Double[] { 10d, 14d, 15d, 23d, 25d, 13d, 47d, 5d, 53d, 17d, 21d, 63d, 89d, 55d, 91d, 79d, 57d };
        Double[] inOrder = new Double[] { 5d, 10d, 13d, 14d, 15d, 17d, 21d, 23d, 25d, 47d, 53d, 55d, 57d, 63d, 79d, 89d, 91d };

        // build an ArrayList of those value and add to BPTree also
        // allows for comparing the contents of the ArrayList 
        // against the contents and functionality of the BPTree
        // does not ensure BPTree is implemented correctly
        // just that it functions as a data structure with
        // insert, rangeSearch, and toString() working.
        for (int i = 0; i < inputs.length; i++) {
        	System.out.println();
        	System.out.println("Inserting " + inputs[i]);
        	System.out.println();
            bpTree.insert(inputs[i], inputs[i]);
            System.out.println("\n\nTree structure:\n" + bpTree.toString());
            System.out.println(bpTree.inOrderPrint());
        }
        for (Double d: inOrder)
        {
        	System.out.print(d + ", ");
        }
        //List<Double> filteredValues = bpTree.rangeSearch(0.2d, ">=");
        //System.out.println("Filtered values: " + filteredValues.toString());
    }

} // End of class BPTree

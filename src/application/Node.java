package application;


public class Node {

		public int x;
		public int y;
		public int index;
		public int depth; //for BFS
		public Node next; //for queue

		public Node parent;
		
		
		
		
		public Node(int x, int y, int index) {
			this.x = x;
			this.y = y;
			this.index = index;
			this.depth = 0;
			this.next = null;
			this.parent = null;
			
		}
		
		public Node(int x, int y, int index, Node parent) {
			this.x = x;
			this.y = y;
			this.index = index;
			this.depth = parent.depth +1;
			this.next = null;
			this.parent = parent;
			
		}



}

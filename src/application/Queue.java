package application;

public class Queue {

		private Node first;
		private Node last;
		public int size = 0;
		
		public Queue(Node root) {
			first = root;
			size = 1;
			last = null;
		}
		
		//method to add a node to a queue
		public void enqueue(Node node) {
			if (first == null) {
				//queue is empty
				first = node;
			}else if (last == null){
				//only one item in queue
				last = node;
				first.next = last;
			}else {
				//many items in queue
				//Node temp = last;
				//temp.next = node;
				last.next = node;
				last = node;
			}
			size++;
		}
		
		//method to dequeue lead Node and return it, exception??!
		public Node dequeue() throws Exception{
			if (first == null){
		        throw new Exception();
		    }
			Node temp = first;
			first = first.next;
			if(first == last||first == null) {last = null;}
			size--;
			return temp;
		}
		
		public Node peek() {
			if (first == null) {
				return null;
			}
			return first;
		}
}

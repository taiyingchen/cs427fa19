package lanSimulation.internals;

public class Printer extends Node{

    /**
     * Construct a <em>Node</em> with given #type and #name.
     */
    public Printer(String _name) {
        super(_name);
        super.className = "Printer";
    }

    /**
     * Construct a <em>Node</em> with given #type and #name, and which is linked
     * to #nextNode.
     */
    public Printer(String _name, Node _nextNode) {
        super(_name, _nextNode);
        super.className = "Printer";
    }

}

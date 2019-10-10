package lanSimulation.internals;

public class Workstation extends Node{

    /**
     * Construct a <em>Node</em> with given #type and #name.
     */
    public Workstation(String _name) {
        super(_name);
        super.className = "Workstation";
    }

    /**
     * Construct a <em>Node</em> with given #type and #name, and which is linked
     * to #nextNode.
     */
    public Workstation(String _name, Node _nextNode) {
        super(_name, _nextNode);
        super.className = "Workstation";
    }

}

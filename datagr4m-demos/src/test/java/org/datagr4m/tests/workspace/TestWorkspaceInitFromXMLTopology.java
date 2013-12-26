package org.datagr4m.tests.workspace;

import org.datagr4m.drawing.layout.runner.ILayoutRunner;
import org.datagr4m.drawing.layout.runner.impl.LayoutRunner;
import org.datagr4m.drawing.layout.runner.impl.LayoutRunnerFactory;
import org.datagr4m.drawing.layout.runner.sequence.LayoutRunnerSequenceSinglePhase;
import org.datagr4m.drawing.layout.runner.stop.MaxStepCriteria;
import org.datagr4m.drawing.model.items.hierarchical.graph.edges.tubes.Tube;
import org.datagr4m.topology.Topology;
import org.datagr4m.topology.graph.IPropertyEdge;
import org.datagr4m.topology.graph.IPropertyNode;
import org.datagr4m.topology.io.TopologyIOXML;
import org.datagr4m.trials.drawing.demo01.topologies.DisplayInitilizer;
import org.datagr4m.workspace.Workspace;
import org.datagr4m.workspace.configuration.ConfigurationFacade.EdgeComputationPolicy;
import org.datagr4m.workspace.configuration.ConfigurationFacade.EdgeRenderingPolicy;
import org.datagr4m.workspace.configuration.ConfigurationFacade.ViewPolicy;
import org.junit.Assert;
import org.junit.Test;

/**
 * Integration test: load an XML topology and verify that the workspace has the
 * appropriate data model.
 * 
 * To ease debugging, one may use: w.getTopology().toConsole();
 * w.getEdgeModel().toConsole();
 * 
 */
public class TestWorkspaceInitFromXMLTopology /* extends TestCase */{
    public static void main(String[] str) throws Exception {
        TestWorkspaceInitFromXMLTopology t = new TestWorkspaceInitFromXMLTopology();
        t.loadAndTest(false, -1);
    }

    @Test
    public void testLoad() throws Exception {
        loadAndTest(true, 10);
    }

    public void loadAndTest(boolean test, int waitFor) throws Exception {
        TopologyIOXML xmlt = new TopologyIOXML();
        Topology<IPropertyNode, IPropertyEdge> topology = xmlt.loadTopology("src/test/resources/topology.xml");
        // topology.toConsole();
        if (test)
            assertTopology(topology, 8, 6, 2, 2);

        Workspace w = new Workspace(topology);
        // w.getEdgeModel().toConsole();
        if (test) {
            assertWorkspaceNotNull(w);
            assertEdgeModel(w);
        }

        display(w);
        
        
        ILayoutRunner runner = w.getRunner();
        /*runner.getConfiguration().getSequence().setFirstPhaseBreakCriteria(new MeanMoveCriteria(1, 10));
        if (test && waitFor > 0)
            ((LayoutRunner) runner).startAndAwaitAtMost(waitFor);
*/
        
        Workspace.defaultRunnerFactory = new LayoutRunnerFactory();
        runner.getConfiguration().setAllowAutoFitAtStepEnd(true);
        LayoutRunnerSequenceSinglePhase seq = (LayoutRunnerSequenceSinglePhase)runner.getConfiguration().getSequence();
        seq.setFirstPhaseBreakCriteria(new MaxStepCriteria(100));
        
        if (test && waitFor > 0)
            ((LayoutRunner) runner).startAndAwaitAtMost(waitFor);
        else
            runner.start();
        
        
        // final tests
        if (test) {
            assertPathes(w);
        }
    }

    public void assertPathes(Workspace w) {
        Tube tube1 = w.getEdgeModel().getRootTubes().get(0);
        Assert.assertTrue(tube1.getPathGeometry().getPointNumber() > 0);
    }

    public void assertEdgeModel(Workspace w) {
        Assert.assertEquals("model contains one tube at level 0", w.getEdgeModel().getRootTubes().size(), 1);

        Tube tube1 = w.getEdgeModel().getRootTubes().get(0);
        Assert.assertEquals("tube at level 0 has 1 children tube", tube1.getChildren().size(), 1);

        Tube tube2 = (Tube) tube1.getChildren().get(0);
        Assert.assertEquals("tube at level 1 has 6 children edge", tube2.getChildren().size(), 6);
    }

    public void assertTopology(Topology<IPropertyNode, IPropertyEdge> topology, int vertexCount, int edgeCount, int groupCount, int depth) {
        Assert.assertEquals(topology.getGraph().getVertexCount(), vertexCount);
        Assert.assertEquals(topology.getGraph().getEdgeCount(), edgeCount);
        Assert.assertEquals(topology.getGroups().size(), groupCount);
        Assert.assertEquals(topology.getDepth(), depth);
    }

    public void assertWorkspaceNotNull(Workspace w) {
        Assert.assertNotNull("has item model", w.getModel());
        Assert.assertNotNull("has edge model", w.getEdgeModel());
        Assert.assertNotNull("has layout", w.getLayout());
        Assert.assertNotNull("has layout with reference on edge model", w.getLayout().getTubeModel());
        Assert.assertNotNull("has layout with an edge layout", w.getLayout().getTubeLayout());
    }

    public void display(Workspace w) {
        DisplayInitilizer di = new DisplayInitilizer(EdgeComputationPolicy.ALWAYS, EdgeRenderingPolicy.ALWAYS, ViewPolicy.AUTOFIT_AT_RUN);
        di.init(w).openFrame();
    }

    {
        Workspace.defaultRunnerFactory = new LayoutRunnerFactory();
    }

}
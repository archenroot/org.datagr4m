package org.datagr4m.drawing.renderer.factories;

import org.datagr4m.drawing.model.items.IBoundedItem;
import org.datagr4m.drawing.model.items.hierarchical.IHierarchicalNodeModel;
import org.datagr4m.drawing.model.items.hierarchical.graph.IHierarchicalGraphModel;
import org.datagr4m.drawing.model.items.hierarchical.pair.IHierarchicalPairModel;
import org.datagr4m.drawing.renderer.items.hierarchical.AbstractHierarchicalRenderer;
import org.datagr4m.drawing.renderer.items.hierarchical.IHierarchicalRenderer;
import org.datagr4m.drawing.renderer.items.hierarchical.graph.HierarchicalGraphRenderer;
import org.datagr4m.drawing.renderer.items.hierarchical.pair.HierarchicalPairRenderer;
import org.datagr4m.viewer.IDisplay;


public class HierarchicalRendererFactory implements IHierarchicalRendererFactory{
    @Override
    public IHierarchicalRenderer getRenderer(IDisplay display, IHierarchicalNodeModel model) {
        if(model instanceof IHierarchicalGraphModel){
            return getHierarchicalGraphRenderer(display, model);
        }
        else if(model instanceof IHierarchicalPairModel){
            return getHierarchicalPairRenderer(display, model);
        }
        return null;
    }

    private IHierarchicalRenderer getHierarchicalPairRenderer(IDisplay display, IHierarchicalNodeModel model) {
        HierarchicalPairRenderer renderer = new HierarchicalPairRenderer(display, (IHierarchicalPairModel)model);
        attachChildren(renderer, model);
        return renderer;
    }

    private IHierarchicalRenderer getHierarchicalGraphRenderer(IDisplay display, IHierarchicalNodeModel model) {
        HierarchicalGraphRenderer renderer=new HierarchicalGraphRenderer(display, (IHierarchicalGraphModel)model);
        attachChildren(renderer, model);
        return renderer;
    }
    
    /** recursively handle children renderers.*/
    protected void attachChildren(AbstractHierarchicalRenderer renderer, IHierarchicalNodeModel model){
        for(IBoundedItem child: model.getChildren()){
            if(child instanceof IHierarchicalNodeModel){
                IHierarchicalNodeModel submodel = (IHierarchicalNodeModel)child;
                IHierarchicalRenderer subrenderer = getRenderer(renderer.getDisplay(), submodel);
                if(subrenderer!=null)
                    renderer.addChild(subrenderer);
            }
        }
    }
}

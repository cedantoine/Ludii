package app.display.dialogs.visual_editor.view.components.ludemenodecomponent;


import app.display.dialogs.visual_editor.model.LudemeNode;
import app.display.dialogs.visual_editor.view.panels.IGraphPanel;
import app.display.dialogs.visual_editor.view.panels.editor.EditorPanel;

import javax.swing.*;
import java.util.Arrays;

public class NodePopupMenu extends JPopupMenu {
    public NodePopupMenu(LudemeNodeComponent nodeComponent, IGraphPanel graphPanel) {
        JMenuItem delete = new JMenuItem("Delete");
        JMenuItem dynamic = new JMenuItem("(Un)Set Dynamic");
        JMenuItem observe = new JMenuItem("Observe");

        add(delete);
        add(dynamic);
        add(observe);

        delete.addActionListener(e -> {
            graphPanel.removeNode(nodeComponent.node());
        });

        dynamic.addActionListener(e -> {
            nodeComponent.changeDynamic();
        });

        observe.addActionListener(e -> {
            LudemeNode node = nodeComponent.node();
            String message = "";
            message += "ID: " + node.id() + "\n";
            message += "Name: " + node.symbol().name() + "\n";
            message += "Constructor: " + node.selectedClause() + "\n";
            message += "Dynamic: " + node.dynamic() + "\n";
            message += "Provided Inputs: " + Arrays.toString(node.providedInputs()) + "\n";
            message += "Provided LIFs: " + nodeComponent.inputArea().providedInputFields + "\n";
            message += "Active C: (" + nodeComponent.inputArea().activeClauses.size() + ") " +nodeComponent.inputArea().activeClauses + "\n";
            message += "Inactive C: (" + + nodeComponent.inputArea().inactiveClauses.size() + ") " + nodeComponent.inputArea().inactiveClauses + "\n";
            message += "Width: " + nodeComponent.width() + "\n";

            JOptionPane.showMessageDialog((EditorPanel) graphPanel, message);
        });

    }
}
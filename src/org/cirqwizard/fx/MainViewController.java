/*
This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License version 3 as published by
    the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.cirqwizard.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.controlsfx.control.action.AbstractAction;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class MainViewController extends ScreenController implements Initializable
{
    @FXML private HBox breadCrumbBarBox;
    @FXML private AnchorPane contentPane;

    private ScreenController currentScreen;

    @Override
    protected String getFxmlName()
    {
        return "MainView.fxml";
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        Action millingAction = new AbstractAction("Milling")
        {
            @Override
            public void handle(ActionEvent event)
            {
                System.out.println(event);
            }
        };
        Action drillingAction = new AbstractAction("Drilling")
        {
            @Override
            public void handle(ActionEvent event)
            {
                System.out.println(event);
            }
        };
        ContextMenu contextMenu = ActionUtils.createContextMenu(Arrays.asList(millingAction, drillingAction));

        Button b = new Button("Home");
        b.setOnAction(event ->
        {
            Button button = (Button) event.getSource();
            final Scene scene = button.getScene();
            final Point2D windowCoord = new Point2D(scene.getWindow().getX(), scene.getWindow().getY());
            final Point2D sceneCoord = new Point2D(scene.getX(), scene.getY());
            final Point2D nodeCoord = button.localToScene(0.0, 0.0);
            final double clickX = Math.round(windowCoord.getX() + sceneCoord.getX() + nodeCoord.getX());
            final double clickY = Math.round(windowCoord.getY() + sceneCoord.getY() + nodeCoord.getY() + button.getHeight());

            contextMenu.show((Node) event.getSource(), clickX, clickY);
        });
        b.getStyleClass().setAll("button", "first-button");
        b.setFocusTraversable(false);
        b.setMaxHeight(Double.MAX_VALUE);
        breadCrumbBarBox.getChildren().add(b);
        breadCrumbBarBox.getChildren().add(b = new Button("Milling"));
        b.setMaxHeight(Double.MAX_VALUE);
        b.setFocusTraversable(false);
        b.getStyleClass().setAll("button", "middle-button");
        breadCrumbBarBox.getChildren().add(b = new Button("Placement"));
        b.setMaxHeight(Double.MAX_VALUE);
        b.setFocusTraversable(false);
        b.getStyleClass().setAll("button", "last-button");
    }

    public ScreenController getCurrentScreen()
    {
        return currentScreen;
    }

    public void setScreen(ScreenController screen)
    {
        this.currentScreen = screen;

        contentPane.getChildren().clear();
        screen.refresh();
        contentPane.getChildren().add(screen.getView());
        AnchorPane.setTopAnchor(contentPane.getChildren().get(0), 0.0);
        AnchorPane.setLeftAnchor(contentPane.getChildren().get(0), 0.0);
        AnchorPane.setRightAnchor(contentPane.getChildren().get(0), 0.0);
        AnchorPane.setBottomAnchor(contentPane.getChildren().get(0), 0.0);
        updateBreadCrumbBar(getPath(screen));

    }

    private List<ScreenController> getPath(ScreenController scene)
    {
        ArrayList<ScreenController> path = new ArrayList<>();
        for (; scene != null; scene = scene.getParent())
            path.add(0, scene);
        return path;
    }

    private void updateBreadCrumbBar(List<ScreenController> path)
    {
        breadCrumbBarBox.getChildren().clear();
        for (int i = 0; i < path.size(); i++)
        {
            final ScreenController item = path.get(i);
            Button b = new Button(item.getName());
            b.setFocusTraversable(false);
            b.getStyleClass().setAll("button");
            if (path.size() == 1)
                b.getStyleClass().add("only-button");
            else if (i == 0)
                b.getStyleClass().add("first-button");
            else if (i == path.size() - 1)
                b.getStyleClass().add("last-button");
            else
                b.getStyleClass().addAll("middle-button");

            List<Action> contextMenuActions = new ArrayList<>();
            List<ScreenController> siblings = getMainApplication().getSiblings(item);
            if (siblings != null)
            {
                for (ScreenController sibling : siblings)
                {
                    AbstractAction action = new AbstractAction(sibling.getName())
                    {
                        @Override
                        public void handle(ActionEvent event)
                        {
                            sibling.select();
                        }
                    };
                    action.setDisabled(!sibling.isEnabled());
                    contextMenuActions.add(action);
                }
                ContextMenu contextMenu = ActionUtils.createContextMenu(contextMenuActions);
                b.setOnAction(event ->
                {
                    Button button = (Button) event.getSource();
                    final Scene scene = button.getScene();
                    final Point2D windowCoord = new Point2D(scene.getWindow().getX(), scene.getWindow().getY());
                    final Point2D sceneCoord = new Point2D(scene.getX(), scene.getY());
                    final Point2D nodeCoord = button.localToScene(0.0, 0.0);
                    final double clickX = Math.round(windowCoord.getX() + sceneCoord.getX() + nodeCoord.getX());
                    final double clickY = Math.round(windowCoord.getY() + sceneCoord.getY() + nodeCoord.getY() + button.getHeight());
                    contextMenu.show((Node) event.getSource(), clickX, clickY);
                });
            }
            else
                b.setOnAction(event -> item.select());

            breadCrumbBarBox.getChildren().add(b);
        }
    }

}
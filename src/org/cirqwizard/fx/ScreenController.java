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

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.cirqwizard.logging.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public abstract class ScreenController
{
    @FXML protected Parent view;

    private MainApplication mainApplication;
    private ScreenController parent;
    private List<ScreenController> children = new ArrayList<>();

    public ScreenController()
    {
        if (getFxmlName() != null)
        {
            try
            {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(getFxmlName()));
                loader.setController(this);
                loader.load();
            }
            catch (IOException e)
            {
                LoggerFactory.logException("FXML loading failed: ", e);
            }
        }
    }

    public Parent getView()
    {
        return view;
    }

    public ScreenController setMainApplication(MainApplication mainApplication)
    {
        this.mainApplication = mainApplication;
        return this;
    }

    public ScreenController getParent()
    {
        return parent;
    }

    public void setParent(ScreenController parent)
    {
        this.parent = parent;
    }

    public List<ScreenController> getChildren()
    {
        return children;
    }

    public ScreenController addChild(ScreenController child)
    {
        child.setParent(this);
        children.add(child);
        return this;
    }

    protected String getFxmlName()
    {
        return null;
    }

    protected String getName()
    {
        return null;
    }

    public MainApplication getMainApplication()
    {
        return mainApplication;
    }

    public void refresh()
    {
    }

    public boolean isActive()
    {
        return this == getMainApplication().getSceneController(getMainApplication().getState().getScene());
    }

    public void goBack()
    {
        if (isActive())
            getMainApplication().prevState();
    }

    public void next()
    {
        getMainApplication().setCurrentScreen(getMainApplication().getNext(getMainApplication().getCurrentScreen()));
//        MainViewController mainViewController = (MainViewController) getMainApplication().getSceneController(SceneEnum.MainView);
//        mainViewController.setCurrentScreen(getMainApplication().getNext(mainViewController.getCurrentScreen()));
//        if (isActive())
//            getMainApplication().nextState();
    }
}

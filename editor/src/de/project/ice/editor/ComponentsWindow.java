package de.project.ice.editor;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;
import de.project.ice.ecs.IceEngine;
import de.project.ice.ecs.components.*;
import de.project.ice.editor.undoredo.AddComponentAction;
import de.project.ice.editor.undoredo.RemoveComponentAction;
import de.project.ice.editor.undoredo.UndoRedoManager;
import org.jetbrains.annotations.Nullable;


public class ComponentsWindow extends VisWindow
{
    @SuppressWarnings("unchecked")
    public static Class<? extends Component>[] components = new Class[]{
            AnimationComponent.class,
            BreathComponent.class,
            CameraComponent.class,
            ControlComponent.class,
            HotspotComponent.class,
            IdleComponent.class,
            InvisibilityComponent.class,
            MoveComponent.class,
            NameComponent.class,
            ScriptComponent.class,
            TextureComponent.class,
            TransformComponent.class,
            WalkAreaComponent.class,
            WalkingComponent.class,
    };

    private Entity entity = null;
    private VisTable componentsTable;
    private IceEngine engine;
    private UndoRedoManager undoRedoManager;

    public ComponentsWindow(IceEngine engine, UndoRedoManager undoRedoManager) throws IllegalStateException
    {
        super("Components");
        this.engine = engine;
        this.undoRedoManager = undoRedoManager;

        TableUtils.setSpacingDefaults(this);
        createWidgets();

        setPosition(0, 0, Align.bottomRight);
        setResizable(true);
    }

    @Override
    public void act(float delta)
    {
        super.act(delta);
    }

    private void createWidgets()
    {
        componentsTable = new VisTable();
        componentsTable.defaults().align(Align.topLeft);


        final PopupMenu menu = new PopupMenu();

        for (final Class<? extends Component> component : components)
        {
            MenuItem item = new MenuItem(component.getSimpleName(), new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event, Actor actor)
                {
                    undoRedoManager.addAction(new AddComponentAction(engine.createComponent(component), entity));
                    updateEntity();
                }
            });
            menu.addItem(item);
        }

        final VisTextButton btnAddComponent = new VisTextButton("Add component", new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                Vector2 stageCoord = localToStageCoordinates(new Vector2(actor.getX(), actor.getY()));
                menu.showMenu(getStage(), stageCoord.x, stageCoord.y);
            }
        });

        add(btnAddComponent).expandX().fill().row();
        VisScrollPane scrollPane = new VisScrollPane(componentsTable);
        row().expand();
        add(scrollPane).fill();
    }

    public void setEntity(@Nullable Entity entity)
    {
        this.entity = entity;
        updateEntity();
    }

    private void updateEntity()
    {
        componentsTable.clear();

        if (entity == null)
        {
            getTitleLabel().setText("Components");
            return;
        }

        String name = new EntityEntry(entity).getName();
        getTitleLabel().setText("Components of " + name);

        for (final Component component : entity.getComponents())
        {
            String componentName = component.getClass().getSimpleName();
            VisLabel label = new VisLabel(componentName);
            componentsTable.add(label).expandX();
            VisTextButton removeBtn = new VisTextButton("-", new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event, Actor actor)
                {
                    undoRedoManager.addAction(new RemoveComponentAction(component, entity));
                    updateEntity();
                }
            });
            componentsTable.add(removeBtn).padRight(20f);
            componentsTable.row();
            ComponentTable table = new ComponentTable(component);
            componentsTable.add(table).padLeft(10f).colspan(2).row();
        }
        componentsTable.row();
        componentsTable.add(new VisLabel("")).expand();
    }
}
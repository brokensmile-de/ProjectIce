package de.project.ice.editor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;
import de.project.ice.ecs.IceEngine;
import org.jetbrains.annotations.Nullable;


public class EntitiesWindow extends VisWindow {
    private ImmutableArray<Entity> entities;
    private Array<EntityEntry> currentEntities = new Array<EntityEntry>();
    private VisList<EntityEntry> entityList;
    private EntityEntry selectedEntry = null;
    private SelectionListener selectionListener = null;
    private IceEngine engine;

    public EntitiesWindow(IceEngine engine) throws IllegalStateException {
        super("Entities");
        this.engine = engine;
        this.entities = engine.getEntities();

        TableUtils.setSpacingDefaults(this);
        createWidgets();

        setPosition(0, 0, Align.topRight);
        setResizable(true);
        setSize(200, 400);
    }

    public void setSelectionListener(SelectionListener selectionListener) {
        this.selectionListener = selectionListener;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (entitiesChanged())
            updateEntities();
        if (selectedEntry != entityList.getSelected()) {
            selectedEntry = entityList.getSelected();
            if (selectionListener != null)
                selectionListener.selectionChanged(selectedEntry == null ? null : selectedEntry.entity);

        }
    }

    private boolean entitiesChanged() {
        // Entities amount changed => we definitely need to update
        if (entities.size() != currentEntities.size)
            return true;

        for (int i = 0; i < entities.size(); ++i) {
            if (entities.get(i) != currentEntities.get(i).entity) {
                return true;
            } else if(!currentEntities.get(i).name.equals(EntityEntry.generateName(entities.get(i)))) {
                currentEntities.get(i).name = EntityEntry.generateName(entities.get(i));
            }
        }

        return false;
    }

    private void updateEntities() {
        currentEntities.clear();

        for(Entity entity : entities)
            currentEntities.add(new EntityEntry(entity));

        entityList.setItems(currentEntities);
    }

    private void createWidgets() {

        VisTextButton createEntityBtn = new VisTextButton("Create Entity", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                engine.addEntity(engine.createEntity());
            }
        });
        add(createEntityBtn).expandX().fill().row();


        entityList = new VisList<EntityEntry>();
        entityList.setWidth(Float.MAX_VALUE);
        updateEntities();

        VisScrollPane scrollPane = new VisScrollPane(entityList);

        row().expand();
        add(scrollPane).fill();
    }

    public interface SelectionListener {
        void selectionChanged(@Nullable Entity entity);
    }
}
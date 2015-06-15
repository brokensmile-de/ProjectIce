package de.project.ice.dialog;

import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Node {
    public enum Type {
        Start, Node, Branch, Choice, Text, Set
    }
    @NotNull
    public Type type = Type.Node;
    @NotNull
    public final String id;
    @Nullable
    public Node next = null;
    @Nullable
    public String variable_name = null;
    @Nullable
    public String variable_value = null;
    @NotNull
    public String text = "";
    @NotNull
    public final Array<Node> choices = new Array<Node>(0);
    @Nullable
    public Branch branch = null;

    public Node( @NotNull String id) {
        this.id = id;
    }

}
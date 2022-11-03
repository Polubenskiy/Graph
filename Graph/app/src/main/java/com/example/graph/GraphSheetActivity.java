package com.example.graph;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.example.graph.classes.DatabaseHelper;
import com.example.graph.classes.GraphView;
import com.example.graph.model.Graph;

public class GraphSheetActivity extends AppCompatActivity
{
    Intent intent;
    GraphView graphView;
    DatabaseHelper databaseHelper;
    public int graphID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        intent = getIntent();
        graphID = intent.getIntExtra("graphID", -1);
        graphView = findViewById(R.id.graphView);

        databaseHelper = new DatabaseHelper(this, "Graphs.db", null, 1);
        graphView.graph = databaseHelper.SelectNode(graphID);
        graphView.invalidate();
    }

    public void on_Add_Node_click(View v)
    {
        if (graphView.linkSelect)
            graphView.remove_select_link();

        graphView.add_note();

        databaseHelper.UpdateCountNodesGraph(graphID, graphView.graph.nodes.size());
        databaseHelper.AddNode(graphID, graphView.graph);
    }

    public void on_Remove_Node_click(View v)
    {
        graphView.remove_select_node();
    }

    public void on_Rename_Node_click(View v)
    {
        final String[] name = new String[1];

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Change node text");
        alert.setMessage("Enter new node text");

        //Create TextView
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                name[0] = input.getText().toString();
                graphView.change_node_text(name[0]);
                graphView.invalidate();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton) { }
        });
        alert.show();
    }

    public void on_Add_Link_click(View v)
    {
        graphView.add_link();
    }

    public void on_One_Link_click(View v) { graphView.one_side_link();}

    public void on_DoubleSideLink_click(View v) { graphView.double_side_link();}

    public void on_Remove_Link_click(View v)
    {
        graphView.remove_select_link();
    }

    public void on_Edit_Link_click(View v)
    {
        final float[] value = new float[1];
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Link value");
        alert.setMessage("Insert new link value");
        //Create TextView
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                value[0] =  Float.parseFloat(input.getText().toString());
                graphView.change_link_weight(value[0]);
                graphView.invalidate();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton) { }
        });
        alert.show();
    }

    public void on_Clear_click(View v)
    {
        graphView.graph.links.clear();
        graphView.graph.nodes.clear();
        graphView.invalidate();
    }
}




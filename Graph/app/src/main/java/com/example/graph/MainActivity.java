package com.example.graph;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;


import com.example.graph.classes.DatabaseHelper;
import com.example.graph.model.Graph;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

	public DatabaseHelper databaseHelper;
	public ListView listViewGraphs;
	public ArrayAdapter<Graph> arrayAdapterGraphs;
	public List<Graph> listGraphs;

	public Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getSupportActionBar().setSubtitle("Created by Polubenskiy Daniil 493");

		databaseHelper = new DatabaseHelper(this, "Graphs.db", null, 1);
		listGraphs = databaseHelper.GetGraphsList();

		listViewGraphs = findViewById(R.id.listViewGraph);
		arrayAdapterGraphs = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listGraphs);
		listViewGraphs.setAdapter(arrayAdapterGraphs);

		listViewGraphs.setOnItemClickListener
		(
			new AdapterView.OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int position, long id)
				{
					int graphID = arrayAdapterGraphs.getItem(position).id;
					intent = new Intent(MainActivity.this, GraphSheetActivity.class);
					intent.putExtra("graphID", graphID);
					startActivity(intent);
				}
			}
		);
	}

	public void on_Create_Graph_Click(View v)
	{
		final String[] name = new String[1];
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Title");
		alert.setMessage("Enter the name of the graph");

		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialogInterface, int whichButton)
			{
				name[0] = input.getText().toString();

				Graph graph = new Graph();
				graph.name = name[0];
				graph.timestamp = new Date().getTime();
				int graphID = databaseHelper.AddGraph(graph);
				graph.id = graphID;

				listGraphs.add(graph);
				arrayAdapterGraphs.notifyDataSetChanged();
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialogInterface, int whichButton) { }
		});
		alert.show();
	}
}
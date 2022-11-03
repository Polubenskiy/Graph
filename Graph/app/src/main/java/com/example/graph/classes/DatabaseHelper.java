package com.example.graph.classes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.graph.model.Graph;
import com.example.graph.model.Link;
import com.example.graph.model.Node;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper
{
	public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version)
	{
		super(context,
				name,
				factory,
				version);
	}

	@Override
	public void onCreate(SQLiteDatabase database)
	{
		String sql = "CREATE TABLE Graphs (ID integer PRIMARY KEY AUTOINCREMENT, " +
				"Name TEXT NOT NULL, " +
				"CountNodes INTEGER NOT NULL, " +
				"CountLinks INTEGER NOT NULL, " +
				"Date REAL NOT NULL);";
		database.execSQL(sql);

		sql = "CREATE TABLE Nodes (ID integer PRIMARY KEY AUTOINCREMENT, " +
				"NodesGraphID  INTEGER, " +
				"NodesLocationX REAL NOT NULL, " +
				"NodesLocationY REAL NOT NULL, " +
				"NodesName TEXT, " +
				"FOREIGN KEY (NodesGraphID) REFERENCES Graphs(ID));";
		database.execSQL(sql);

		sql = "CREATE TABLE Links (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"LinksGraphID INTEGER NOT NULL, " +
				"LinkNodeA INTEGER not null, " +
				"LinkNodeB INTEGER not null, " +
				"LinkOneSide Bool, " +
				"LinkDoubleSide Bool, " +
				"LinkWeight REAL,  " +
				"FOREIGN KEY (LinksGraphID) REFERENCES Graphs(ID), " +
				"FOREIGN KEY (LinkNodeA) REFERENCES Nodes(id));";
		database.execSQL(sql);
	}

	public int AddGraph(Graph graph)  //returns Graph ID in Table
	{
		SQLiteDatabase database = getWritableDatabase();
		String sql = "INSERT INTO Graphs (Name, CountNodes, CountLinks, Date) VALUES ('" + graph.name + "', '"
				+ graph.nodes.size() + "', '"
				+ graph.links.size() + "', '"
				+ graph.timestamp + "');";
		database.execSQL(sql);

		Log.e("TES","Inserted new graph");
		return GetGraphID(graph);
	}

	public void UpdateCountNodesGraph(int ID, int count)
	{
		SQLiteDatabase database = getReadableDatabase();

		String sql = "UPDATE Graphs SET CountNodes = " + count + " WHERE ID = " + ID + ";";
		database.execSQL(sql);
	}

	private int GetGraphID(Graph graph)
	{
		SQLiteDatabase database = getReadableDatabase();
		String sql = "SELECT ID from Graphs WHERE Date = " + graph.timestamp + ";";

		Cursor cur = database.rawQuery(sql, null);
		if (cur.moveToFirst())
		{
			Log.e("TEST","ID RETRIEVED SUCCESSFUL");
			return cur.getInt(0);
		}
		return -1;
	}

	public List<Graph> GetGraphsList()
	{
		List<Graph> graphs = new ArrayList<>();

		SQLiteDatabase database = getReadableDatabase();
		String sql = "SELECT * FROM Graphs;";

		Cursor cursor = database.rawQuery(sql, null);
		if (cursor.moveToFirst())
		{
			do
			{
				Graph graph = new Graph();
				graph.id = cursor.getInt(0);
				graph.name = cursor.getString(1);
				graph.timestamp = cursor.getInt(2);
				graphs.add(graph);
			} while (cursor.moveToNext());
		}

		return graphs;
	}

	public Graph GetGraph(int id)
	{
		Graph graph = new Graph();
		SQLiteDatabase database = getReadableDatabase();

		String sql = "SELECT * FROM Graphs WHERE ID + " + id +";";
		Cursor cur = database.rawQuery(sql,null);

		if (cur.moveToFirst())
		{
			graph.id = cur.getInt(0);
			graph.name = cur.getString(1);
			graph.timestamp = cur.getLong(2);
		}

		sql = "SELECT * FROM Nodes WHERE NodesGraphId = "+ id +";";
		cur = database.rawQuery(sql,null);

		if (cur.moveToFirst() == true)
		{
			do
			{
				Node node = new Node(cur.getFloat(2),cur.getFloat(3));
				String text = cur.getString(4);

				if (text.equals("null")) text = null;
				node.SetName(text);
				graph.nodes.add(node);

			}while (cur.moveToNext() == true);
		}

		sql = "SELECT * FROM Links WHERE LinksGraphId = '"+ id +"';";
		cur = database.rawQuery(sql,null);

		if (cur.moveToFirst() == true)
		{
			do
			{
				Link link = new Link(cur.getInt(2),cur.getInt(3));
				float value = cur.getFloat(4);
				link.SetWeight(value);
				graph.links.add(link);

			}while (cur.moveToNext() == true);
		}
		return graph;
	}

	public int SaveGraph(Graph graph)
	{
		DeleteGraph(graph.id);
		graph.timestamp = new Date().getTime();

		SQLiteDatabase database = getWritableDatabase();

		String sql = "INSERT INTO Graphs(id,Name,date) VALUES ("+ graph.id + ",'"+ graph.name + "',"+ graph.timestamp + ");";
		database.execSQL(sql);

		for (int i = 0; i < graph.nodes.size(); i++)
		{
			Node node = graph.nodes.get(i);
			sql = "INSERT INTO Nodes(graphID,LocationX,LocationY,Text) VALUES("+ graph.id +","+ node.X +","+ node.Y +",'"+ node.name +"');";
			database.execSQL(sql);
		}

		for (int i = 0; i < graph.links.size(); i++)
		{
			Link l = graph.links.get(i);
			sql = "INSERT INTO Links(graphID,NodeA,NodeB,value) VALUES("+ graph.id +","+ l.a +","+ l.b +","+ l.weight +");";
			database.execSQL(sql);
		}
		return GetGraphID(graph);
	}

	public void DeleteGraph(int id)
	{
		String sql = "";
		SQLiteDatabase database = getWritableDatabase();

		sql = "DELETE FROM Links WHERE LinksGraphID = " + id + ";";
		database.execSQL(sql);

		sql = "DELETE FROM Nodes WHERE NodesGraphID = " + id + ";";
		database.execSQL(sql);

		sql = "DELETE FROM Graphs WHERE GraphID = " + id + ";";
		database.execSQL(sql);

		Log.i("user", "Graph " + id + " delete from database");
	}

	public void AddNode(int graphID, Graph graph)
	{


		SQLiteDatabase database = getReadableDatabase();
		for (int i = 0; i < graph.nodes.size(); i++)
		{
			Node node = graph.nodes.get(i);
				String sql = "INSERT INTO Nodes(NodesGraphID, " +
					"NodesLocationX, " +
					"NodesLocationY, " +
					"NodesName) " +
					"VALUES("+ graphID +","+
					node.X +","+
					node.Y +",'"+
					node.name +"');";
			database.execSQL(sql);
		}
	}

	public Graph SelectNode(int graphID)
	{
		SQLiteDatabase database = getReadableDatabase();

		String sql = "SELECT * FROM Nodes WHERE NodesGraphID = " + graphID + ";";
		Cursor cursor = database.rawQuery(sql, null);

		Graph graph = GetGraph(graphID);

		if (cursor.moveToFirst())
		{
			do
			{
				Node node = new Node(cursor.getFloat(2), cursor.getFloat(3));
				String nameNode = cursor.getString(4);

				if (nameNode.equals("null")) nameNode = null;

				node.SetName(nameNode);
				graph.nodes.add(node);
			}while (cursor.moveToNext());
		}

		return  graph;
	}

	public void UpdataLocationNode(Graph graph, float locationX, float locationY)
	{
		SQLiteDatabase database = getWritableDatabase();

		String sql = "UPDATE Nodes SET NodesLocationX = " + locationX + " WHERE NodesGraphID = " + graph.id + ";";
		database.execSQL(sql);

		sql = "UPDATE Nodes SET NodesLocationY = " + locationY + " WHERE NodesGraphID = " + graph.id + ";";
		database.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {	}
}

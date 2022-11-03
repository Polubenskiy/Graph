package com.example.graph.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Graph
{
    public ArrayList<Node> nodes = new ArrayList<>();
    public ArrayList<Link> links = new ArrayList<>();

    public int id;
    public String name;
    public long timestamp;

    public void add_Node(float X, float Y)
    {
        nodes.add(new Node(X, Y));
    }

    public void remove_node(int index)
    {
        if (index < 0) return;
        nodes.remove(index);
    }

    public void add_Link(int firstNode, int secondNode)
    {
        links.add(new Link(firstNode, secondNode));
    }

    public void delete_Link(int index)
    {
        if (index < 0) return;
        links.remove(index);
    }

    @Override
    public String toString()
    {
        Timestamp timestamp = new Timestamp(this.timestamp);
        Date date = new Date(timestamp.getTime());
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

        return this.name;
    }

    public Graph CopyGraph()
    {
        Graph graph = new Graph();
        graph.name = this.name + " copy";
        graph.timestamp = new java.util.Date().getTime();
        graph.nodes = new ArrayList<Node>();

        for (int i = 0; i < this.nodes.size(); i++)
        {
            Node nodeOriginal = this.nodes.get(i);
            Node nodeNew = new Node(nodeOriginal.X, nodeOriginal.Y);
            nodeNew.name = nodeOriginal.name;
            graph.nodes.add(nodeNew);
        }

        for (int i = 0; i < this.links.size(); i++)
        {
            Link linkOriginal = this.links.get(i);
            Link linkNew = new Link(linkOriginal.a, linkOriginal.b);
            linkNew.weight = linkOriginal.weight;
            graph.links.add(linkNew);
        }

        return graph;
    }
}


package com.example.graph.model;

public class Node
{
    public float X;
    public float Y;
    public String name;

    public Node(float x, float y)
    {
        this.X = x;
        this.Y = y;
    }

    public Node(float x, float y, String name)
    {
        this.X = x;
        this.Y = y;
        this.name = name;
    }

    public void SetName(String name)
    {
        this.name = name;
    }
}

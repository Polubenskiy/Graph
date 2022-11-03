package com.example.graph.model;

public class Link
{
    public int a;
    public int b;
    public float weight;
    public boolean isDirectional = false;
    public boolean isDoubleSide = false;
    public boolean isSelected = false;
    public float X, Y;

    public Link(int a, int b)
    {
        this.a = a;
        this.b = b;
    }

    public Link(int a, int b, float weight)
    {
        this.a = a;
        this.b = b;
        this.weight = weight;
    }

    public void SetWeight(float weight)
    {
        this.weight = weight;
    }
}

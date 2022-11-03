package com.example.graph.classes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.widget.Toast;

import com.example.graph.model.Graph;
import com.example.graph.model.Link;
import com.example.graph.model.Node;

public class GraphView extends SurfaceView
{
    public Graph graph = new Graph();
    public Paint paint, mPaint;

    public Float last_X;
    public Float last_Y;

    public int selected1 = -1;
    public int selected2 = -1;
    public int lastSelectNode = -1;

    public boolean linkSelect = false;
    public int linkID = -1;

    public float radius = 50.0f;
    public float radiusForLink = 20.0f;

    public GraphView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(25.0f); //размер шрифта для canvas.drawText()
        paint.setTextAlign(Paint.Align.CENTER);


        mPaint = new Paint();
        mPaint.setAntiAlias(true); //включить сглаживание
        mPaint.setColor(Color.argb(50, 0, 127, 255));
        mPaint.setStrokeWidth(2f); //ширина контура 2 пикселя
        mPaint.setStyle(Paint.Style.FILL); //закрашивать примитивы черным цветом
        mPaint.setTextSize(100f); //размер шрифта для canvas.drawText()

        setWillNotDraw(false);
    }

    public void add_note()
    {
        graph.add_Node(300.0f, 300.0f);
        invalidate();
    }

    public int get_node_at_xy(float x, float y)
    {
        for (int i = graph.nodes.size() - 1; i >= 0; i--)
        {
            Node n = graph.nodes.get(i);
            float dx = x - n.X;
            float dy = y - n.Y;

            if (dx * dx + dy * dy <= radius * radius) return i;
        }
        return -1;
    }

    public void change_node_text(String text)
    {
        if (selected1 == -1 || linkSelect)
        {
            Toast toast = Toast.makeText(this.getContext(), "No nodes selected\nor link selected", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        Node node = graph.nodes.get(lastSelectNode);
        node.name = text;
        invalidate();
    }

    public void remove_select_node()
    {
        if (selected1 < 0) return;
        if (linkSelect) return;
        for (int i = 0; i < graph.links.size(); i++)
        {
            graph.links.remove(i);
        }
        graph.remove_node(selected1);
        selected1 = -1;
        invalidate();
    }

    public void add_link()
    {
        if (selected1 < 0) return;
        if (selected2 < 0) return;
        if (linkSelect) return;

        for (int i = 0; i < graph.links.size(); i++)
        {
            Link link = graph.links.get(i);
            if (selected1 == link.a && selected2 == link.b) return;
            if (selected1 == link.b && selected2 == link.a) return;
        }

        graph.add_Link(selected1, selected2);
        selected1 = -1;
        selected2 = -1;
        invalidate();
    }

    public void one_side_link()
    {
        if (linkID < 0 || !linkSelect) return;

        Link link = graph.links.get(linkID);

        if (link.isDirectional)
        {
            link.isDirectional = false;
        }
        else
        {
            link.isDirectional = true;
        }
        invalidate();
    }

    public void double_side_link()
    {
        if (linkID < 0 || linkSelect == false) return;

        Link link = graph.links.get(linkID);

        if (link.isDoubleSide)
        {
            link.isDoubleSide = false;
        }
        else
        {
            link.isDoubleSide = true;
        }
        invalidate();
    }


    public int get_link_at_xy(float x, float y)
    {
        for (int i = 0; i < graph.links.size(); i++)
        {
            Link link = graph.links.get(i);

            float dx = x - link.X;
            float dy = y - link.Y;

            if (dx * dx + dy * dy <= radiusForLink * radiusForLink) return i;
        }
        return -1;
    }

    public void change_link_weight(float value)
    {
        if (linkSelect)
        {
            Link link = graph.links.get(linkID);
            link.SetWeight(value);
            invalidate();
        }
        else
        {
            Toast toast = Toast.makeText(this.getContext(), "No nodes selected\nor link selected", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void remove_select_link()
    {
        if (linkID < 0) return;

        graph.links.remove(linkID);
        invalidate();
    }

    public void clear_all()
    {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int action = event.getAction();

        float x = event.getX();
        float y = event.getY();

        switch(action)
        {
            case MotionEvent.ACTION_DOWN:
            {
                linkSelect = false;

                int linkID = get_link_at_xy(x, y);
                Log.i("user", "LinkID " + linkID);

                if (linkID > -1) // link is down
                {
                    linkSelect = true;
                    selected1 = -1;
                    selected2 = -1;
                    this.linkID = linkID;
                    Log.i("user", "LinkID " + linkID);
                    invalidate();
                    return true;
                }

                if (linkID < 0)
                {
                    linkSelect = false;
                    this.linkID = -1;
                    invalidate();
                }

                int nodeID = get_node_at_xy(x, y);
                Log.i("user", "nodeID " + nodeID);

                lastSelectNode = -1;
                int tempSelect1 = -1;
                int tempSelect2 = -1;

                Log.i("user", "lastSelectNode " + lastSelectNode);
                Log.i("user", "tempSelect1 " + tempSelect1);
                Log.i("user", "tempSelect2 " + tempSelect2);

                if (nodeID < 0)
                {
                    selected1 = tempSelect1;
                    selected2 = tempSelect2;
                    lastSelectNode = -1;

                    Log.i("user", "lastSelectNode " + lastSelectNode);
                    Log.i("user", "tempSelect1 " + tempSelect1);
                    Log.i("user", "tempSelect2 " + tempSelect2);
                    invalidate();
                    return true;
                }

                if (nodeID == selected1)
                {
                    tempSelect1 = nodeID;
                    Log.i("user", "nodeID" + nodeID);
                    Log.i("user", "lastSelectNode " + lastSelectNode);
                    Log.i("user", "tempSelect1 " + tempSelect1);
                    Log.i("user", "tempSelect2 " + tempSelect2);

                }

                if (selected1 >= 0 && nodeID != selected1)
                {
                    tempSelect2 = nodeID;
                    selected2 = tempSelect2;
                    tempSelect1 = selected1;
                    Log.i("user", "nodeID" + nodeID);
                    Log.i("user", "lastSelectNode " + lastSelectNode);
                    Log.i("user", "tempSelect1 " + tempSelect1);
                    Log.i("user", "tempSelect2 " + tempSelect2);
                }

                if (selected2 < 0) tempSelect1 = nodeID;

                lastSelectNode = nodeID;
                selected1 = tempSelect1;
                selected2 = tempSelect2;
                last_X = x;
                last_Y = y;

                Log.i("user", "nodeID " + nodeID);
                Log.i("user", "lastSelectNode " + lastSelectNode);
                Log.i("user", "tempSelect1 " + tempSelect1);
                Log.i("user", "tempSelect2 " + tempSelect2);

                invalidate();
                return true;
            }

            case MotionEvent.ACTION_UP: break;

            case MotionEvent.ACTION_MOVE:
            {
                if (lastSelectNode >= 0)
                {
                    Node node = graph.nodes.get(lastSelectNode);
                    node.X += x - last_X;
                    node.Y += y - last_Y;
                    invalidate();
                }
                last_X = x;
                last_Y = y;

                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawColor(Color.rgb(255, 255, 255));

        for (int i = 0; i < graph.links.size(); i++)
        {
            Link link = graph.links.get(i);

            Node node_a = graph.nodes.get(link.a);
            Node node_b = graph.nodes.get(link.b);

            float x0 = node_a.X;
            float y0 = node_a.Y;
            float x1 = node_b.X;
            float y1 = node_b.Y;

            float longX0 = x1 - x0;
            float longY0 = y1 - y0;
            float longX1 = x0 - x1;
            float longY1 = y0 - y1;

            double hypotinuse = Math.sqrt(Math.pow(longX1, 2) + Math.pow(longY1, 2));

            double coeff1 = radius / hypotinuse;

            double newPointX0 = node_a.X + longX0 * coeff1;
            double newPointY0 = node_a.Y + longY0 * coeff1;

            double newPointX1 = node_b.X + longX1 * coeff1;
            double newPointY1 = node_b.Y + longY1 * coeff1;

            paint.setColor(Color.argb(50, 0, 127, 255));

            Log.i("user", "isDirectional" + link.isDirectional);
            if (link.isDirectional)
            {
                float[] coordinationArrow = CreateArrow(hypotinuse, node_b, longX1, longY1, newPointX1, newPointY1);

                canvas.drawLine((float)newPointX1, (float)newPointY1, coordinationArrow[0], coordinationArrow[1], paint);
                canvas.drawLine((float)newPointX1, (float)newPointY1, coordinationArrow[2], coordinationArrow[3], paint);

                Path path = new Path();
                path.moveTo((float)newPointX1, (float)newPointY1);
                path.lineTo(coordinationArrow[0], coordinationArrow[1]);
                path.lineTo(coordinationArrow[2], coordinationArrow[3]);
                path.moveTo((float)newPointX1, (float)newPointY1);
                canvas.drawPath(path, mPaint);

                if (link.isDoubleSide)
                {
                    float[] coordinationArrowBack = CreateArrow(hypotinuse, node_a, longX0, longY0, newPointX0, newPointY0);

                    canvas.drawLine((float)newPointX0, (float)newPointY0, coordinationArrowBack[0], coordinationArrowBack[1], mPaint);
                    canvas.drawLine((float)newPointX0, (float)newPointY0, coordinationArrowBack[2], coordinationArrowBack[3], mPaint);


                    path.moveTo((float)newPointX0, (float)newPointY0);
                    path.lineTo(coordinationArrowBack[0], coordinationArrowBack[1]);
                    path.lineTo(coordinationArrowBack[2], coordinationArrowBack[3]);
                    path.moveTo((float)newPointX0, (float)newPointY0);
                    canvas.drawPath(path, mPaint);
                }
            }

            canvas.drawLine((float)newPointX0, (float)newPointY0, (float)newPointX1, (float)newPointY1, paint);

            float centerX = (x1 + x0) * 0.5f;
            float centerY = (y1 + y0) * 0.5f;

            link.X = centerX;
            link.Y = centerY;

            paint.setStyle(Paint.Style.FILL);
            if (i == linkID) paint.setColor(Color.argb(50, 127, 0, 255));
            else paint.setColor(Color.argb(50, 0, 127, 255));

            canvas.drawCircle(link.X, link.Y, radiusForLink, paint);

            if (link.weight != 0.0f)
            {
                canvas.drawText(String.valueOf(link.weight), centerX, (float) (centerY + (radiusForLink * 4)), paint);
            }

            invalidate();
        }

        for (int i = 0; i < graph.nodes.size(); i++)
        {
            Node node = graph.nodes.get(i);

            paint.setStyle(Paint.Style.FILL);

            if (i == selected1) paint.setColor(Color.argb(50, 127, 0, 255));
            else if (i == selected2) paint.setColor(Color.argb(50, 255, 0, 50));
            else paint.setColor(Color.argb(50, 0, 127, 255));

            canvas.drawCircle(node.X, node.Y, radius, paint);

            if (node.name != null) canvas.drawText(node.name, node.X, node.Y + (2 * radius), paint);

            paint.setStyle(Paint.Style.STROKE);

            if (i == selected1) paint.setColor(Color.rgb(127, 0, 255));
            else paint.setColor(Color.rgb(0, 127, 255));

            canvas.drawCircle(node.X, node.Y, radius, paint);

            invalidate();
        }
    }

    public float[] CreateArrow(double hypotinuse, Node node_b, float longX, float longY, double newPointX, double newPointY)
    {
        float[] coordinationArrow = new float[4];

        double coeff2 = (radius + 25) / hypotinuse;
        double vspPointX = node_b.X + longX * coeff2;
        double vspPointY = node_b.Y + longY * coeff2;

        double difX0 = vspPointX - newPointX;
        double tempDifX = difX0;
        double difY0 = vspPointY - newPointY;
        double tempDifY = difY0;

        difX0 = vspPointX + (tempDifY / 2);
        difY0 = vspPointY - (tempDifX / 2);

        double difX1 = vspPointX - (tempDifY / 2);
        double difY1 = vspPointY + (tempDifX / 2);

        coordinationArrow[0] = (float) difX0;
        coordinationArrow[1] = (float) difY0;
        coordinationArrow[2] = (float) difX1;
        coordinationArrow[3] = (float) difY1;

        return coordinationArrow;
    }
}

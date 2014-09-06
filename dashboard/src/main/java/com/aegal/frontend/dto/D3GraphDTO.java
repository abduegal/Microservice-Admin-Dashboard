package com.aegal.frontend.dto;

import java.io.Serializable;
import java.util.List;

/**
 * DTO to create a d3 graph.
 * User: A.Egal
 * Date: 8/10/14
 * Time: 4:20 PM
 */
public class D3GraphDTO<T> implements Serializable{

    public String id;
    public int group;
    public String name;
    public String location;
    public int size;
    public T data;
    public List<T> links;

}

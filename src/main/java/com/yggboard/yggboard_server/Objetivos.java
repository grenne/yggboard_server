package com.yggboard.yggboard_server;

import org.json.simple.JSONArray;

import com.mongodb.BasicDBObject;

public interface Objetivos {

	public BasicDBObject get();
	public JSONArray getAll();
	
};

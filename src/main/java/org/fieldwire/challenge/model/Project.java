package org.fieldwire.challenge.model;

import lombok.Data;

import java.util.List;

@Data
public class Project {
	private Long id;
	private String name;
	private List<Floorplan> floorplans;
}

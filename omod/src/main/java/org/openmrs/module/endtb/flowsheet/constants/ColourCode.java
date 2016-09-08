package org.openmrs.module.endtb.flowsheet.constants;

public enum ColourCode {
    GREEN("green"), PURPLE("purple"), YELLOW("yellow"), GREY("grey");

    private String colour;
    private ColourCode(String colour){
        this.colour = colour;
    }

    public String getColourCode() {
        return this.colour;
    }
}

package com.taskmonitor.module.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(max = 50, message = "Name cannot exceed 50 characters")
    private String name;

    @NotBlank(message = "Color hex code is required")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Must be a valid hex color (e.g. #4F46E5)")
    private String colorHex;

    @Size(max = 50, message = "Icon cannot exceed 50 characters")
    private String icon;

    public CategoryRequest() {
    }

    public CategoryRequest(String name, String colorHex, String icon) {
        this.name = name;
        this.colorHex = colorHex;
        this.icon = icon;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}

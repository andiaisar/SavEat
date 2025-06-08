package com.example.saveat.model;

import java.util.List;

public class GeminiRequest {
    private List<Content> contents;
    private GenerationConfig generationConfig;

    public GeminiRequest(List<Content> contents, GenerationConfig generationConfig) {
        this.contents = contents;
        this.generationConfig = generationConfig;
    }

    // Getters
    public List<Content> getContents() {
        return contents;
    }

    public GenerationConfig getGenerationConfig() {
        return generationConfig;
    }

    // Setters
    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    public void setGenerationConfig(GenerationConfig generationConfig) {
        this.generationConfig = generationConfig;
    }

    public static class Content {
        private List<Part> parts;

        public Content(List<Part> parts) {
            this.parts = parts;
        }

        public List<Part> getParts() {
            return parts;
        }

        public void setParts(List<Part> parts) {
            this.parts = parts;
        }
    }

    public static class Part {
        private String text;

        public Part(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class GenerationConfig {
        private double temperature;
        private int maxOutputTokens;
        private double topP;
        private int topK;

        public GenerationConfig(double temperature, int maxOutputTokens, double topP, int topK) {
            this.temperature = temperature;
            this.maxOutputTokens = maxOutputTokens;
            this.topP = topP;
            this.topK = topK;
        }

        // Getters
        public double getTemperature() {
            return temperature;
        }

        public int getMaxOutputTokens() {
            return maxOutputTokens;
        }

        public double getTopP() {
            return topP;
        }

        public int getTopK() {
            return topK;
        }

        // Setters
        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }

        public void setMaxOutputTokens(int maxOutputTokens) {
            this.maxOutputTokens = maxOutputTokens;
        }

        public void setTopP(double topP) {
            this.topP = topP;
        }

        public void setTopK(int topK) {
            this.topK = topK;
        }
    }
}
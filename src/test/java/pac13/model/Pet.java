package pac13.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Pet {
    @JsonProperty("id")
    private int id;
    @JsonProperty("category")
    private pac12.Model.Pet.Category category;
    @JsonProperty("name")
    private String name;
    @JsonProperty("tags")
    private List<pac12.Model.Pet.Tags> tags;
    @JsonProperty("photoUrls")
    private List<String>photoUrls;
    @JsonProperty("status")
    private String status;
    public Pet(){

    }

    public Pet(int id, pac12.Model.Pet.Category category, String name, List<pac12.Model.Pet.Tags> tags, List<String> photoUrls, String status) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.tags = tags;
        this.photoUrls = photoUrls;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public pac12.Model.Pet.Category getCategory() {
        return category;
    }

    public void setCategory(pac12.Model.Pet.Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<pac12.Model.Pet.Tags> getTags() {
        return tags;
    }

    public void setTags(List<pac12.Model.Pet.Tags> tags) {
        this.tags = tags;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Category{
        @JsonProperty("id")
        private int id;
        @JsonProperty("name")
        private String name;
        public Category(){

        }

        public Category(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
    public static class Tags{
        @JsonProperty("id")
        private int id;
        @JsonProperty("name")
        private String name;
        public Tags(){

        }

        public Tags(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

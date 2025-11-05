package api_project.model;

import java.util.List;

public class PetBuilder {
    private long id = 1l;
    private String name = "Default Pet";
    private Pet.Category category = new Pet.Category(1, "default-category");
    private List<String> photoUrls = List.of("https://example.com/photo.png");

    private List<Pet.Tags> tags = List.of(new Pet.Tags(1, "default-tag"));
    private String status = "available";

    public PetBuilder id(long id) {
        this.id = id;
        return this;
    }

    public PetBuilder name(String name) {
        this.name = name;
        return this;
    }

    public PetBuilder category(Pet.Category category) {
        this.category = category;
        return this;
    }

    public PetBuilder photoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
        return this;
    }

    public PetBuilder tags(List<Pet.Tags> tags) {
        this.tags = tags;
        return this;
    }

    public PetBuilder status(String status) {
        this.status = status;
        return this;
    }

    public Pet build() {
        Pet pet = new Pet();
        pet.setId(this.id);
        pet.setName(this.name);
        pet.setCategory(this.category);
        pet.setPhotoUrls(this.photoUrls);
        pet.setTags(this.tags);
        pet.setStatus(this.status);
        return pet;
    }
}

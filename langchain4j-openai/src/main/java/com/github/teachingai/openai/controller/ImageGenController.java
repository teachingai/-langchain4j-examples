package com.github.teachingai.openai.controller;

import com.github.teachingai.openai.request.ImageGenRequest;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.output.Response;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;

@Controller
public class ImageGenController {

    private final ImageModel imageModel;

    public ImageGenController(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    @PostMapping("/imagegen")
    public String imageGen(@RequestBody ImageGenRequest request) {

        Response<Image> response = imageModel.generate(request.prompt());
        URI imageUrl = response.content().url();

        return "redirect:" + imageUrl;
    }


}

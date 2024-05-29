package com.se.demo.controller;

import com.se.demo.dto.ProjectDTO;
import com.se.demo.entity.ProjectEntity;
import com.se.demo.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/create")
    public ProjectDTO createProject(@ModelAttribute ProjectDTO projectDTO){ //@ModelAttribute 사용했으므로 form으로만 가능, JSON으로 받으려면 @RequestBody로 바꿔야함
        //System.out.println("projectDTO = " + projectDTO);
        ProjectEntity savedEntity = projectService.save(projectDTO);
        return projectService.toDTO(savedEntity);
    }

    @GetMapping("/{project_id}")
    public ProjectDTO findByProjectId(@PathVariable int project_id) {
        return projectService.findById(project_id);
    }

    @GetMapping("/my/{user_id}")
    public List<ProjectDTO> findByUserId(@PathVariable int user_id) {
        return projectService.findByUserId(user_id);
    }
}

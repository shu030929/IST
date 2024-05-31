package com.se.demo.service;

import com.se.demo.dto.IssueDTO;
import com.se.demo.dto.MemberDTO;
import com.se.demo.dto.ProjectDTO;
import com.se.demo.dto.ResponseProjectDTO;
import com.se.demo.entity.IssueEntity;
import com.se.demo.entity.MemberEntity;
import com.se.demo.entity.ProjectEntity;
import com.se.demo.repository.IssueRepository;
import com.se.demo.repository.MemberRepository;
import com.se.demo.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final IssueRepository issueRepository;

    private final IssueService issueService;

    //@Transactional
    public ProjectEntity save(ProjectDTO projectDTO) {
        //System.out.println("LEADER::"+projectDTO.getLeader_id());
        MemberEntity leaderEntity = memberRepository.findById(projectDTO.getLeader_id())
            .orElseThrow(() -> new IllegalArgumentException("Invalid leader ID"));
        MemberDTO leaderDTO = MemberDTO.toMemberDTO(leaderEntity);
        projectDTO.getMembers().add(leaderDTO);

        ProjectEntity projectEntity = ProjectEntity.toProjectEntity(projectDTO);
        return projectRepository.save(projectEntity);
    }

    @Transactional
    public ResponseProjectDTO findById(int project_id) {
        ProjectEntity projectEntity = projectRepository.findById(project_id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid project ID"));
        //return toDTO(projectEntity);
        ProjectDTO projectDTO = ProjectDTO.toProjectDTO(projectEntity);

        MemberEntity leaderEntity = memberRepository.findById(projectDTO.getLeader_id())
                .orElseThrow(() -> new IllegalArgumentException("Invalid leader nickname"));

        return new ResponseProjectDTO(projectDTO, leaderEntity.getNickname());
    }

    @Transactional
    public List<ResponseProjectDTO> findByUserId(int userId) {
        MemberEntity memberEntity = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        List <ProjectDTO> projectDTOList = ProjectDTO.toProjectDTOList(memberEntity.getProjects());
        List <ResponseProjectDTO> responseProjectDTOList = new ArrayList<>();

        for(ProjectDTO projectDTO : projectDTOList){
            MemberEntity leaderEntity = memberRepository.findById(projectDTO.getLeader_id())
                .orElseThrow(() -> new IllegalArgumentException("Invalid leader nickname"));
            ResponseProjectDTO responseProjectDTO = new ResponseProjectDTO(projectDTO, leaderEntity.getNickname());
            responseProjectDTOList.add(responseProjectDTO);
        }

        return responseProjectDTOList;
    }

    public List<IssueDTO> findByProjectId(int projectId) {
        List<IssueEntity> issueEntityList = issueRepository.findByProjectId(projectId);
        List<IssueDTO> issueDTOList = issueEntityList.stream()
                .map(IssueDTO::toIssueDTO)
                .collect(Collectors.toList());
        return issueDTOList;
    }

    @Transactional
    public IssueEntity createIssue(IssueDTO issueDTO) {
        //해당 프로젝트DTO의 issueList에 넣어주고

        ProjectEntity projectEntity = projectRepository.findById(issueDTO.getProject_id())
                .orElseThrow(() -> new IllegalArgumentException("Invalid project ID"));
        ProjectDTO projectDTO = ProjectDTO.toProjectDTO(projectEntity);
        projectDTO.getIssues().add(issueDTO);
        //이슈 진짜 생성
        return issueService.createIssue(issueDTO);
    }

    @Transactional
    public ProjectDTO inviteMember(int projectId, int userId) {
        //projectDTO 가져와서 members에 add
        //실제 DB에도 project_members table에 추가
        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid project ID"));
        MemberEntity memberEntity = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        projectEntity.getMembers().add(memberEntity);
        return ProjectDTO.toProjectDTO(projectEntity);
    }

    /*private MemberDTO toMemberDTO(MemberEntity memberEntity) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setUser_id(memberEntity.getUser_id());
        memberDTO.setNickname(memberEntity.getNickname());
        memberDTO.setPassword(memberEntity.getPassword());
        //memberDTO.setProjects(toProjectDTOList(memberEntity.getProjects())); 이거 있어야 나의 project의 member의 project 정보가 올바르게 나오는데, 이거 실행하면 스택오버플로우 발생해서.. 그리고 내 동료의 project들을 내가 굳이 알아야 할 필요는 없잖앙
        return memberDTO;
    }*/

    //없어도 될듯
    /*public ProjectDTO toDTO(ProjectEntity projectEntity) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(projectEntity.getId());
        projectDTO.setTitle(projectEntity.getTitle());
        projectDTO.setLeader_id(projectEntity.getLeader_id());

        projectDTO.setMembers(
                projectEntity.getMembers().stream()
                        .map(member -> new MemberDTO(member.getUser_id(), member.getNickname(), member.getPassword()))
                        .collect(Collectors.toList())
        );
        return projectDTO;
    }*/
/*
    public IssueDTO toIssueDTO(IssueEntity issueEntity) {
        /*return new IssueDTO(
                //issueEntity.getId()
                //나중에 issueEntity 필드 생기면 다 추가
                //issueEntity.getIssue_title(),
                //issueEntity.getIssue_description()

        );
        return IssueDTO.toIssueDTO(issueEntity);
    }*/

    /*@Transactional
    public ProjectDTO toProjectDTO(ProjectEntity projectEntity) {
        List<IssueEntity> issueEntities = projectEntity.getIssues();
        List<IssueDTO> issueDTOs = new ArrayList<>();
        if(issueEntities!=null){
            for(IssueEntity issueEntity : issueEntities) {
                issueDTOs.add(IssueDTO.toIssueDTO(issueEntity));
            }
        }

        List<MemberEntity> memberEntities = projectEntity.getMembers();
        List<MemberDTO> memberDTOs = new ArrayList<>();
        if(memberEntities!=null){
            for(MemberEntity memberEntity : memberEntities) {
                memberDTOs.add(toMemberDTO(memberEntity));
            }
        }

        return new ProjectDTO(
                projectEntity.getId(),
                projectEntity.getTitle(),
                projectEntity.getLeader_id(),
                issueDTOs,
                memberDTOs
        );
    }

    private List<ProjectDTO> toProjectDTOList(List<ProjectEntity> projectEntity){
        List<ProjectDTO> projectDTOs = new ArrayList<>();

        for(ProjectEntity projectEntity1 : projectEntity) {
            toProjectDTO(projectEntity1);
            projectDTOs.add(toProjectDTO(projectEntity1));
        }
        return projectDTOs;
    }*/

}

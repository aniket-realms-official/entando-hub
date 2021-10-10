package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.persistence.BundleGroupRepository;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.service.BundleGroupService;
import com.entando.hub.catalog.service.CategoryService;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/bundlegroups")
public class BundleGroupController {

    private final Logger logger = LoggerFactory.getLogger(BundleGroupController.class);

    private final BundleGroupService bundleGroupService;
    private final CategoryService categoryService;

    public BundleGroupController(BundleGroupService bundleGroupService, CategoryService categoryService) {
        this.bundleGroupService = bundleGroupService;
        this.categoryService = categoryService;
    }

    //@RolesAllowed("codemotion-bff-admin")
    //@PreAuthorize("hasAuthority('ROLE_mf-widget-admin')")
    @CrossOrigin
    @GetMapping("/")
    public List<BundleGroup> getBundleGroups(@RequestParam(required = false) String organisationId) {
        logger.debug("REST request to get BundleGroups by organisation Id: {}", organisationId);
        return bundleGroupService.getBundleGroups(Optional.ofNullable(organisationId)).stream().map(BundleGroup::new).collect(Collectors.toList());
    }


    //@RolesAllowed("codemotion-bff-admin")
    //@PreAuthorize("hasAuthority('ROLE_mf-widget-admin')")
    @CrossOrigin
    @GetMapping("/filtered")
    public PagedContent<BundleGroup, com.entando.hub.catalog.persistence.entity.BundleGroup> getBundleGroupsAndFilterThem(@RequestParam Integer pageNum, @RequestParam Integer pageSize, @RequestParam(required = false) String organisationId, @RequestParam(required = false) String[] categoryIds, @RequestParam(required = false) String[] statuses) {
        Integer sanitizedPageNum = pageNum >=1 ? pageNum-1 : 0;

        String[] categoryIdFilterValues = categoryIds;
        if(categoryIdFilterValues==null){
            categoryIdFilterValues = categoryService.getCategories().stream().map(c->c.getId().toString()).toArray(String[]::new);
        }

        String[] statusFilterValues = statuses;
        if(statusFilterValues == null){
            statuses = Arrays.stream(com.entando.hub.catalog.persistence.entity.BundleGroup.Status.values()).map(Enum::toString).toArray(String[]::new);
        }

        logger.debug("REST request to get BundleGroups by organisation Id: {}, categoryIds {}, statuses {}", organisationId, categoryIds, statuses);
        Page<com.entando.hub.catalog.persistence.entity.BundleGroup> bundleGroupsPage = bundleGroupService.getBundleGroups(sanitizedPageNum, pageSize, Optional.ofNullable(organisationId), categoryIdFilterValues, statuses);
        PagedContent<BundleGroup, com.entando.hub.catalog.persistence.entity.BundleGroup> pagedContent = new PagedContent<>(bundleGroupsPage.getContent().stream().map(BundleGroup::new).collect(Collectors.toList()),bundleGroupsPage);
        return pagedContent;
    }


    @CrossOrigin
    @GetMapping("/{bundleGroupId}")
    public ResponseEntity<BundleGroup> getBundleGroup(@PathVariable String bundleGroupId) {
        logger.debug("REST request to get BundleGroup by Id: {}", bundleGroupId);
        Optional<com.entando.hub.catalog.persistence.entity.BundleGroup> bundleGroupOptional = bundleGroupService.getBundleGroup(bundleGroupId);
        if (bundleGroupOptional.isPresent()) {
            return new ResponseEntity<>(bundleGroupOptional.map(BundleGroup::new).get(), HttpStatus.OK);
        } else {
            logger.warn("Requested bundleGroup '{}' does not exists", bundleGroupId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }


    @CrossOrigin
    @PostMapping("/")
    public ResponseEntity<BundleGroup> createBundleGroup(@RequestBody BundleGroupNoId bundleGroup) {
        logger.debug("REST request to create BundleGroup: {}", bundleGroup);
        com.entando.hub.catalog.persistence.entity.BundleGroup saved = bundleGroupService.createBundleGroup(bundleGroup.createEntity(Optional.empty()), bundleGroup);
        return new ResponseEntity<>(new BundleGroup(saved), HttpStatus.CREATED);
    }

    @CrossOrigin
    @PostMapping("/{bundleGroupId}")
    public ResponseEntity<BundleGroup> updateBundle(@PathVariable String bundleGroupId, @RequestBody BundleGroupNoId bundleGroup) {
        logger.debug("REST request to update BundleGroup with id {}: {}", bundleGroupId, bundleGroup);
        Optional<com.entando.hub.catalog.persistence.entity.BundleGroup> bundleGroupOptional = bundleGroupService.getBundleGroup(bundleGroupId);
        if (!bundleGroupOptional.isPresent()) {
            logger.warn("BundleGroup '{}' does not exists", bundleGroupId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            com.entando.hub.catalog.persistence.entity.BundleGroup saved = bundleGroupService.createBundleGroup(bundleGroup.createEntity(Optional.of(bundleGroupId)), bundleGroup);
            return new ResponseEntity<>(new BundleGroup(saved), HttpStatus.OK);
        }
    }


    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class BundleGroup extends BundleGroupNoId {
        private final String bundleGroupId;

        public BundleGroup(String bundleGroupId, String name, String description, String descriptionImage) {
            super(name, description, descriptionImage);
            this.bundleGroupId = bundleGroupId;
        }

        public BundleGroup(com.entando.hub.catalog.persistence.entity.BundleGroup entity) {
            super(entity);
            this.bundleGroupId = entity.getId().toString();
        }
    }

    @Data
    public static class BundleGroupNoId {
        protected final String name;
        protected final String description;
        protected final String descriptionImage;
        protected String documentationUrl;
        protected com.entando.hub.catalog.persistence.entity.BundleGroup.Status status;

        //the following must be merged with the entity using mappedBy
        protected List<String> children;
        protected String organisationId;
        protected List<String> categories;


        public BundleGroupNoId(String name, String description, String descriptionImage) {
            this.name = name;
            this.description = description;
            this.descriptionImage = descriptionImage;
        }


        public BundleGroupNoId(com.entando.hub.catalog.persistence.entity.BundleGroup entity) {
            this.description = entity.getDescription();
            this.descriptionImage = entity.getDescriptionImage();
            this.name = entity.getName();
            this.status = entity.getStatus();
            this.documentationUrl = entity.getDocumentationUrl();

            if (entity.getOrganisation() != null) {
                this.organisationId = entity.getOrganisation().getId().toString();
            }
            //todo one single iteration
            if (entity.getBundles() != null) {
                this.children = entity.getBundles().stream().map((children) -> children.getId().toString()).collect(Collectors.toList());
            }
            if (entity.getCategories() != null) {
                this.categories = entity.getCategories().stream().map((category) -> category.getId().toString()).collect(Collectors.toList());
            }
        }

        public com.entando.hub.catalog.persistence.entity.BundleGroup createEntity(Optional<String> id) {
            com.entando.hub.catalog.persistence.entity.BundleGroup ret = new com.entando.hub.catalog.persistence.entity.BundleGroup();
            ret.setDescription(this.getDescription());
            ret.setName(this.getName());
            ret.setDescriptionImage(this.getDescriptionImage());
            ret.setDocumentationUrl(this.getDocumentationUrl());
            ret.setStatus(this.getStatus());
            if (this.organisationId != null) {
                Organisation organisation = new Organisation();
                organisation.setId(Long.parseLong(this.organisationId));
                ret.setOrganisation(organisation);
            }
            id.map(Long::valueOf).ifPresent(ret::setId);
            return ret;
        }
    }


}

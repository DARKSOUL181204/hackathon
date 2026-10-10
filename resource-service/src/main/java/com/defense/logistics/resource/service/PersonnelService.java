package com.defense.logistics.resource.service;
import com.defense.logistics.resource.dto.PersonnelDto;
import com.defense.logistics.resource.exception.ResourceNotFoundException;
import com.defense.logistics.resource.model.Personnel;
import com.defense.logistics.resource.model.Unit;
import com.defense.logistics.resource.repository.PersonnelRepository;
import com.defense.logistics.resource.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonnelService {
    private final PersonnelRepository repo;
    private final UnitRepository unitRepo;

    @Transactional
    public PersonnelDto create(PersonnelDto dto) {
        if(repo.findByServiceNumber(dto.getServiceNumber()).isPresent()) throw new IllegalArgumentException("Service number exists");
        Unit unit = unitRepo.findById(dto.getUnitId()).orElseThrow(() -> new ResourceNotFoundException("Unit not found"));
        Personnel p = Personnel.builder().serviceNumber(dto.getServiceNumber()).fullName(dto.getFullName()).role(dto.getRole()).unit(unit).posting(dto.getPosting()).availabilityStatus(dto.getAvailabilityStatus()).contactDetails(dto.getContactDetails()).build();
        p = repo.save(p);
        dto.setId(p.getId());
        return dto;
    }

    public PersonnelDto getById(Long id) {
        Personnel p = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Personnel not found"));
        PersonnelDto dto = new PersonnelDto(); dto.setId(p.getId()); dto.setServiceNumber(p.getServiceNumber()); dto.setFullName(p.getFullName()); dto.setRole(p.getRole()); dto.setUnitId(p.getUnit().getId()); dto.setPosting(p.getPosting()); dto.setAvailabilityStatus(p.getAvailabilityStatus()); dto.setContactDetails(p.getContactDetails());
        return dto;
    }
    public List<PersonnelDto> getAll() {
        return repo.findAll().stream().map(e -> {
            PersonnelDto dto = new PersonnelDto();
            dto.setId(e.getId()); dto.setServiceNumber(e.getServiceNumber()); dto.setFullName(e.getFullName()); dto.setRole(e.getRole()); dto.setUnitId(e.getUnit().getId()); dto.setPosting(e.getPosting()); dto.setAvailabilityStatus(e.getAvailabilityStatus()); dto.setContactDetails(e.getContactDetails());
            return dto;
        }).collect(Collectors.toList());
    }
    @Transactional
    public PersonnelDto updateAvailability(Long id, com.defense.logistics.resource.model.enums.AvailabilityStatus status) {
        Personnel person = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Personnel not found"));
        person.setAvailabilityStatus(status);
        return getById(repo.save(person).getId());
    }
}

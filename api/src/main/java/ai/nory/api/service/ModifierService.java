package ai.nory.api.service;

import ai.nory.api.dto.modifier.ModifierDto;
import ai.nory.api.entity.Modifier;
import ai.nory.api.mapper.ModifierDtoMapper;
import ai.nory.api.repository.ModifierRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ModifierService {
    private final ModifierRepository modifierRepository;

    public List<ModifierDto> getModifiers() {
        List<Modifier> modifiers = modifierRepository.findAll();
        return ModifierDtoMapper.INSTANCE.fromEntity(modifiers);
    }
}

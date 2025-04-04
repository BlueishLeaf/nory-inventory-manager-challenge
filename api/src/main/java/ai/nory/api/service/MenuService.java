package ai.nory.api.service;

import ai.nory.api.dto.menu.MenuItemDto;
import ai.nory.api.entity.MenuItem;
import ai.nory.api.mapper.MenuItemDtoMapper;
import ai.nory.api.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuItemRepository menuItemRepository;

    public List<MenuItemDto> getMenuItems(Long locationId) {
        List<MenuItem> menuItems = menuItemRepository.findByKeyLocationId(locationId);
        return MenuItemDtoMapper.INSTANCE.fromEntity(menuItems);
    }
}

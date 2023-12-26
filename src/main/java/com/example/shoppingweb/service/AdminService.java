package com.example.shoppingweb.service;

import com.example.shoppingweb.domain.Authority;
import com.example.shoppingweb.domain.Item;
import com.example.shoppingweb.domain.ItemCategory;
import com.example.shoppingweb.domain.User;
import com.example.shoppingweb.persistance.AuthorityRepository;
import com.example.shoppingweb.persistance.ItemRepository;
import com.example.shoppingweb.persistance.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

@Service
public class AdminService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void deleteItem(int id) {
        itemRepository.deleteById(id);
    }

    @Transactional
    public void updateItem(Item item) {
        Item finditem = itemRepository.findById(item.getId()).get();

        finditem.setItemName(item.getItemName());
        finditem.setPrice(item.getPrice());
        finditem.setDiscountPercent(item.getDiscountPercent());
        finditem.setDiscountPrice(item.getDiscountPrice());
        finditem.setExplaination(item.getExplaination());
        finditem.setCategory(item.getCategory());

        itemRepository.save(finditem);
    }

    @Transactional(readOnly = true)
    public Page<Item> getItemList(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Item> getItemListByCategory(String category, Pageable pageable) {
        ItemCategory itemCategory = ItemCategory.valueOf(category.toUpperCase());
        return itemRepository.findByCategory(itemCategory, pageable);
    }

    @Transactional
    public void deleteUser(int id) {
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("해당 ID의 사용자가 존재하지 않습니다.");
        }
    }

    @Transactional
    public User updateUser(User user) {
        User findUser = userRepository.findById(user.getId()).orElse(null);
        if (findUser != null) {
            findUser.setUsername(user.getUsername());
            findUser.setPassword(passwordEncoder.encode(user.getPassword()));
            findUser.setEmail(user.getEmail());

            // 사용자의 권한을 업데이트
            Authority userAuthority = user.getAuthority();
            if (userAuthority != null) {
                Authority existingAuthority = authorityRepository.findByAuthorityName(userAuthority.getAuthorityName());
                findUser.setAuthority(existingAuthority);
            }
            System.out.println("Service incame" + user.getAuthority().getAuthorityName());

            return findUser;
        } else {
            throw new IllegalArgumentException("해당 사용자를 찾을 수 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public User getUserById(int id) {
        // 검색결과가 없을 때 빈 User 객체로 반환
        User findUser = userRepository.findById(id).orElseGet(
                new Supplier<User>() {
                    @Override
                    public User get() {
                        return new User();
                    }
                });
        return findUser;
    }

    public Page<Item> getItems(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    public Page<User> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}

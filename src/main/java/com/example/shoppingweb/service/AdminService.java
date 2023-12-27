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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Supplier;

@Service
public class AdminService {
    private static final String IMAGE_DIRECTORY = "C:/Works/shoppingItem/";

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

    @Transactional
    public void insertItem(Item item, MultipartFile file) {
        // 먼저 아이템 정보를 저장
        itemRepository.save(item);
        System.out.println("item insert:" + item);

        // 이미지 파일 저장
        if (!file.isEmpty()) {
            try {
                String imagePath = saveImage(file, item.getId());
                item.setImagePath(imagePath);  // 이미지 경로를 아이템에 설정
                itemRepository.save(item);     // 이미지 경로가 업데이트된 아이템 저장
                System.out.println("item insert after saving image:" + item);
            } catch (IOException e) {
                // 파일 저장 중 오류 처리
            }
        }
    }

    private String saveImage(MultipartFile file, int itemId) throws IOException {
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String fileName = itemId + (fileExtension.isEmpty() ? "" : "." + fileExtension);
        Path destinationPath = Paths.get(IMAGE_DIRECTORY + fileName);

        Files.copy(file.getInputStream(), destinationPath);

        return fileName;  // 또는 필요에 따라 전체 경로를 반환할 수 있음
    }

    private String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
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

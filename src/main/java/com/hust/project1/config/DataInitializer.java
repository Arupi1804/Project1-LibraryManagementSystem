package com.hust.project1.config;

import com.hust.project1.entity.Book;
import com.hust.project1.entity.BookCopy;
import com.hust.project1.entity.Member;
import com.hust.project1.entity.User;
import com.hust.project1.entity.User.UserRole;
import com.hust.project1.repository.BookCopyRepository;
import com.hust.project1.repository.BookRepository;
import com.hust.project1.repository.MemberRepository;
import com.hust.project1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private BookRepository bookRepository;

        @Autowired
        private BookCopyRepository bookCopyRepository;

        @Autowired
        private MemberRepository memberRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) throws Exception {
                // Create default ADMIN user if not exists
                if (!userRepository.existsByUsername("admin")) {
                        User admin = new User();
                        admin.setUsername("admin");
                        admin.setPassword(passwordEncoder.encode("admin123"));
                        admin.setEmail("admin@library.com");
                        admin.setRole(UserRole.ROLE_ADMIN);
                        userRepository.save(admin);
                        System.out.println("Default ADMIN user created: admin/admin123");
                }

                // Create default LIBRARIAN user if not exists
                if (!userRepository.existsByUsername("librarian")) {
                        User librarian = new User();
                        librarian.setUsername("librarian");
                        librarian.setPassword(passwordEncoder.encode("librarian123"));
                        librarian.setEmail("librarian@library.com");
                        librarian.setRole(UserRole.ROLE_LIBRARIAN);
                        userRepository.save(librarian);
                        System.out.println("Default LIBRARIAN user created: librarian/librarian123");
                }

                // Create sample books if database is empty
                if (bookRepository.count() == 0) {
                        createSampleBook("978-604-2-13456-7", "Lập trình Java cơ bản", "Nguyễn Văn A",
                                        "NXB Giáo dục", 2020, "Công nghệ thông tin",
                                        "Giáo trình lập trình Java cho người mới bắt đầu");

                        createSampleBook("978-604-2-13457-8", "Cơ sở dữ liệu", "Trần Thị B",
                                        "NXB Đại học Quốc gia", 2021, "Công nghệ thông tin",
                                        "Kiến thức cơ bản về cơ sở dữ liệu và SQL");

                        createSampleBook("978-604-2-13458-9", "Trí tuệ nhân tạo", "Lê Văn C",
                                        "NXB Khoa học và Kỹ thuật", 2022, "Công nghệ thông tin",
                                        "Giới thiệu về AI và Machine Learning");

                        createSampleBook("978-604-2-13459-0", "Văn học Việt Nam", "Phạm Thị D",
                                        "NXB Văn học", 2019, "Văn học",
                                        "Tổng quan văn học Việt Nam qua các thời kỳ");

                        createSampleBook("978-604-2-13460-6", "Lịch sử thế giới", "Hoàng Văn E",
                                        "NXB Chính trị Quốc gia", 2020, "Lịch sử",
                                        "Lịch sử thế giới từ cổ đại đến hiện đại");

                        createSampleBook("978-604-2-13461-3", "Toán cao cấp", "Đỗ Thị F",
                                        "NXB Đại học Quốc gia", 2021, "Toán học",
                                        "Giáo trình toán cao cấp cho sinh viên");

                        createSampleBook("978-604-2-13462-0", "Vật lý đại cương", "Vũ Văn G",
                                        "NXB Giáo dục", 2020, "Vật lý",
                                        "Kiến thức vật lý cơ bản cho sinh viên");

                        createSampleBook("978-604-2-13463-7", "Kinh tế học vi mô", "Bùi Thị H",
                                        "NXB Thống kê", 2022, "Kinh tế",
                                        "Nguyên lý kinh tế vi mô cơ bản");

                        System.out.println("Sample books created successfully!");
                }

                // Create sample book copies if database is empty
                if (bookCopyRepository.count() == 0) {
                        List<Book> allBooks = bookRepository.findAll();
                        if (!allBooks.isEmpty()) {
                                // Create 2-3 copies for each book
                                for (Book book : allBooks) {
                                        createSampleBookCopy(book, "001", BookCopy.CopyStatus.AVAILABLE,
                                                        "Kệ A1 - Tầng 2");
                                        createSampleBookCopy(book, "002", BookCopy.CopyStatus.AVAILABLE,
                                                        "Kệ A1 - Tầng 2");

                                        // Some books have 3 copies with different statuses
                                        if (allBooks.indexOf(book) % 2 == 0) {
                                                createSampleBookCopy(book, "003", BookCopy.CopyStatus.BORROWED,
                                                                "Kệ A2 - Tầng 2");
                                        }
                                }
                                System.out.println("Sample book copies created successfully!");
                        }
                }

                // Create sample members if database is empty
                if (memberRepository.count() == 0) {
                        createSampleMember("DG001", "Nguyễn Văn An", "nguyenvanan@email.com", "0901234567",
                                        "Hà Nội", Member.MemberStatus.ACTIVE);
                        createSampleMember("DG002", "Trần Thị Bình", "tranthib@email.com", "0912345678",
                                        "Hồ Chí Minh", Member.MemberStatus.ACTIVE);
                        createSampleMember("DG003", "Lê Văn Cường", "levancuong@email.com", "0923456789",
                                        "Đà Nẵng", Member.MemberStatus.ACTIVE);
                        createSampleMember("DG004", "Phạm Thị Dung", "phamthidung@email.com", "0934567890",
                                        "Hải Phòng", Member.MemberStatus.SUSPENDED);
                        createSampleMember("DG005", "Hoàng Văn Em", "hoangvanem@email.com", "0945678901",
                                        "Cần Thơ", Member.MemberStatus.ACTIVE);
                        createSampleMember("DG006", "Đỗ Thị Phương", "dothiphuong@email.com", "0956789012",
                                        "Huế", Member.MemberStatus.EXPIRED);
                        createSampleMember("DG007", "Vũ Văn Giang", "vuvangiang@email.com", "0967890123",
                                        "Nha Trang", Member.MemberStatus.ACTIVE);
                        createSampleMember("DG008", "Bùi Thị Hà", "buithiha@email.com", "0978901234",
                                        "Vũng Tàu", Member.MemberStatus.ACTIVE);

                        System.out.println("Sample members created successfully!");
                }
        }

        private void createSampleBook(String isbn, String title, String author,
                        String publisher, Integer publishYear,
                        String category, String description) {
                Book book = new Book();
                book.setIsbn(isbn);
                book.setTitle(title);
                book.setAuthor(author);
                book.setPublisher(publisher);
                book.setPublishYear(publishYear);
                book.setCategory(category);
                book.setDescription(description);
                bookRepository.save(book);
        }

        private void createSampleBookCopy(Book book, String copyNumber, BookCopy.CopyStatus status, String location) {
                BookCopy bookCopy = new BookCopy();
                bookCopy.setBook(book);
                bookCopy.setCopyNumber(book.getIsbn() + "-" + copyNumber);
                bookCopy.setStatus(status);
                bookCopy.setLocation(location);
                bookCopyRepository.save(bookCopy);
        }

        private void createSampleMember(String memberCode, String fullName, String email,
                        String phone, String address, Member.MemberStatus status) {
                Member member = new Member();
                member.setMemberCode(memberCode);
                member.setFullName(fullName);
                member.setEmail(email);
                member.setPhone(phone);
                member.setAddress(address);
                member.setStatus(status);
                // registrationDate and expiryDate will be auto-set by @PrePersist
                memberRepository.save(member);
        }
}

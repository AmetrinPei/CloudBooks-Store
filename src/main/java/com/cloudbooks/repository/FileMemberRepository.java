package com.cloudbooks.repository;
import com.cloudbooks.domain.Member;
import java.nio.file.Path;

public class FileMemberRepository extends InMemoryMemberRepository {
    private final Path file;
    public FileMemberRepository(Path file) {
        this.file = file;
        for (Member member : FileStorage.<Member>load(file)) store.put(member.getId(), member);
    }
    @Override
    public void save(Member member) { super.save(member); FileStorage.persist(file, findAll()); }
}
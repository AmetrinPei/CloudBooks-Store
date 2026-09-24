package com.cloudbooks.repository;

import com.cloudbooks.domain.Member;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description://TODO create:2026/9/23 11:32
 * author:lenovo
 * version：V1.0
 **/
public class InMemoryMemberRepository implements MemberRepository {
    protected final Map<String, Member> store = new LinkedHashMap<>();
    @Override
    public void save(Member entity) {
        store.put(entity.getId(), entity);
    }

    @Override
    public Member findById(String id) {
        return store.get(id);
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }
}

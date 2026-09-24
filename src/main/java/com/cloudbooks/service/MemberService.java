package com.cloudbooks.service;
import com.cloudbooks.domain.Member;
import com.cloudbooks.exception.BookstoreException;
import com.cloudbooks.repository.MemberRepository;
import java.util.List;
/**
 * @description://TODO create:2026/9/23 17:09
 * author:lenovo
 * version：V1.0
 **/
public class MemberService {
    private final MemberRepository repository;
    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }
    public Member register(String name) throws BookstoreException{
        if (name == null || name.isBlank()){
            throw new BookstoreException("会员姓名不能为空");
        }
        Member member = new Member(nextId(),name);
        repository.save(member);
        return member;
    }
    public void recharge(String id,double amount) throws BookstoreException{
        Member member = requireMember(id);
        if (amount <= 0){throw new BookstoreException("充值金额必须大于0");        }
        member.setBalance(round2(member.getBalance()+amount));
        repository.save(member);
    }
    public Member requireMember(String id) throws BookstoreException {
        Member member = repository.findById(id);
        if (member == null) throw new BookstoreException("会员不存在：" + id);
        return member;
    }
    public List<Member> listMembers() { return repository.findAll(); }
    /** 从现有数量 +1 开始找第一个未占用的编号 */
    private String nextId() {
        int n = repository.findAll().size() + 1;
        while (repository.findById(String.format("M%04d", n)) != null) {
            n++;
        }
        return String.format("M%04d", n);
    }
    private static double round2(double value) { return Math.round(value * 100) / 100.0; }
}

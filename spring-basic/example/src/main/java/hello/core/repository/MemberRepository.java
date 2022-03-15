package hello.core.repository;

import hello.core.domain.member.Member;

public interface MemberRepository {

  void save(Member member);

  Member findById(Long id);
}

package com.github.yeokyeong_yoon.snap_influencer_api.repository;

import com.github.yeokyeong_yoon.snap_influencer_api.domain.Influencer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerRepository extends JpaRepository<Influencer, Long> {

} 
package com.xiamu.spring.config.sentinel.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class SentinelConfig {
    @PostConstruct
    public void init() {
        // 创建限流规则列表
        List<FlowRule> rules = new ArrayList<>();

        // 创建限流规则
        FlowRule rule = new FlowRule();
        rule.setResource("getUserInfo"); // 指定需要限流的资源名称
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS); // 设置限流模式为QPS模式
        rule.setCount(1); // 设置限流阈值为每秒1次
        rules.add(rule);

        // 加载限流规则到FlowRuleManager
        FlowRuleManager.loadRules(rules);
        //System.out.println("Loaded flow rules: " + FlowRuleManager.getRules());
    }
}
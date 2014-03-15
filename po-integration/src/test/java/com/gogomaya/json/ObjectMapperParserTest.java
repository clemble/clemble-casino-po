package com.gogomaya.json;

import java.io.IOException;

import com.clemble.casino.base.ActionLatch;
import com.clemble.casino.game.*;
import com.clemble.casino.game.iterator.GamePlayerIterator;
import com.clemble.casino.game.rule.bet.LimitedBetRule;
import com.clemble.casino.game.rule.construct.PlayerNumberRule;
import com.clemble.casino.game.rule.construct.PrivacyRule;
import com.clemble.casino.game.rule.giveup.GiveUpRule;
import com.clemble.casino.game.rule.outcome.DrawRule;
import com.clemble.casino.game.rule.outcome.WonRule;
import com.clemble.casino.game.rule.time.MoveTimeRule;
import com.clemble.casino.game.rule.time.TimeBreachPunishment;
import com.clemble.casino.game.rule.time.TotalTimeRule;
import com.clemble.casino.game.rule.visibility.VisibilityRule;
import com.clemble.casino.game.specification.GameConfigurationKey;
import com.clemble.casino.game.specification.RoundGameConfiguration;
import com.clemble.casino.payment.money.Currency;
import com.clemble.casino.payment.money.Money;
import com.clemble.casino.po.PoBoard;
import com.clemble.casino.po.PoState;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.clemble.casino.event.Event;
import com.clemble.casino.json.ObjectMapperUtils;
import com.clemble.test.random.ObjectGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperParserTest {

    final private String SPECIAL = "{\"type\":\"changed\",\"session\":{\"game\":\"pic\",\"session\":\"aaa4192a123-1489-44e2-974f-07bb05c9d1e4\"},\"state\":{\"type\":\"picPacPoe\",\"context\":{\"clock\":{\"clocks\":[{\"player\":\"aaaec05e038-ab57-46e3-bd99-372a33b37b13\",\"timeSpent\":0,\"moveStart\":0},{\"player\":\"aaa497e60f1-ac06-42e1-8c9c-aa6c2fdc2acf\",\"timeSpent\":0,\"moveStart\":0}]},\"account\":{\"type\":\"visible\",\"bank\":{\"currency\":\"FakeMoney\",\"amount\":0},\"playerAccounts\":[{\"player\":\"aaaec05e038-ab57-46e3-bd99-372a33b37b13\",\"left\":35,\"spent\":1,\"owned\":0},{\"player\":\"aaa497e60f1-ac06-42e1-8c9c-aa6c2fdc2acf\",\"left\":34,\"spent\":2,\"owned\":3}]},\"playerIterator\":{\"type\":\"sequential\",\"index\":2,\"players\":[\"aaaec05e038-ab57-46e3-bd99-372a33b37b13\",\"aaa497e60f1-ac06-42e1-8c9c-aa6c2fdc2acf\"]},\"actionLatch\":{\"actions\":[{\"type\":\"expected\",\"player\":\"aaaec05e038-ab57-46e3-bd99-372a33b37b13\",\"action\":\"select\"}]}},\"board\":{\"type\":\"PoBoard\",\"board\":[[{\"type\":\"exposed\",\"owner\":\"aaa497e60f1-ac06-42e1-8c9c-aa6c2fdc2acf\",\"bets\":[{\"type\":\"bet\",\"player\":\"aaaec05e038-ab57-46e3-bd99-372a33b37b13\",\"bet\":1},{\"type\":\"bet\",\"player\":\"aaa497e60f1-ac06-42e1-8c9c-aa6c2fdc2acf\",\"bet\":2}]},{\"type\":\"state\",\"owner\":\"casino\"},{\"type\":\"state\",\"owner\":\"casino\"}],[{\"type\":\"state\",\"owner\":\"casino\"},{\"type\":\"state\",\"owner\":\"casino\"},{\"type\":\"state\",\"owner\":\"casino\"}],[{\"type\":\"state\",\"owner\":\"casino\"},{\"type\":\"state\",\"owner\":\"casino\"},{\"type\":\"state\",\"owner\":\"casino\"}]],\"selected\":{\"type\":\"cell\",\"row\":0,\"column\":0}},\"outcome\":null,\"version\":3},\"actions\":[{\"type\":\"expected\",\"player\":\"aaaec05e038-ab57-46e3-bd99-372a33b37b13\",\"action\":\"select\"}]}";
    private ObjectMapper objectMapper = ObjectMapperUtils.createObjectMapper();

    @Ignore
    public void specialCase() throws JsonParseException, JsonMappingException, IOException {
        objectMapper.readValue(SPECIAL, Event.class);
    }

    @Test
    public void testProcessing() throws IOException {
        RoundGameContext context = new RoundGameContext(
                ObjectGenerator.generate(GameSessionKey.class),
                ObjectGenerator.generateList(RoundGamePlayerContext.class, 2),
                ObjectGenerator.generate(GamePlayerIterator.class),
                ObjectGenerator.generate(ActionLatch.class), null);
        PoState state = new PoState(context, new PoBoard(), 0);
        String json = objectMapper.writeValueAsString(state);
        PoState desState = (PoState) objectMapper.readValue(json, GameState.class);
        Assert.assertEquals(state, desState);
    }

    @Test
    public void generateConfiguration() throws JsonProcessingException {
        RoundGameConfiguration configuration = new RoundGameConfiguration(
                new GameConfigurationKey(Game.pic, "low"),
                Money.create(Currency.FakeMoney, 36),
                LimitedBetRule.create(1, 36),
                GiveUpRule.all,
                new MoveTimeRule(4000, TimeBreachPunishment.loose),
                new TotalTimeRule(180000, TimeBreachPunishment.loose),
                PrivacyRule.everybody,
                PlayerNumberRule.two,
                VisibilityRule.visible,
                DrawRule.owned,
                WonRule.price,
                ImmutableList.of("X", "O"),
                null);
        System.out.println(objectMapper.writeValueAsString(configuration));
    }

}

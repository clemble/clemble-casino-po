    DELETE FROM GAME_CONFIGURATION WHERE GAME_NAME = 'pic';

    INSERT INTO GAME_CONFIGURATION (GAME_NAME, SPECIFICATION_NAME, CONFIGURATION)
    VALUES ('pic', 'low', '{"type":"match","configurationKey":{"game":"pic","specificationName":"low"},"price":{"currency":"FakeMoney","amount":36},"betRule":{"betType":"limited","minBet":1,"maxBet":36},"giveUpRule":{"giveUp":"all"},"moveTimeRule":{"rule":"moveTime","limit":4000,"punishment":"loose"},"totalTimeRule":{"rule":"totalTime","limit":180000,"punishment":"loose"},"privacyRule":["privacy","everybody"],"numberRule":["participants","two"],"visibilityRule":"visible","drawRule":["DrawRule","owned"],"wonRule":["WonRule","price"],"roles":["X","O"]}');


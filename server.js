var dgram 	= require('dgram')
	, util 		= require('util')
	, net			= require('net')
	, events 	= require('events')
	, redis		= require('redis')
	, pg 			= require('pg')
	, msgTags	= require('./messageTags.js');

var redisClient = redis.createClient();

var playerSnitchEntries = {};
var playerSnitchLogins = {};
var playerSnitchLogouts = {};
var playerSessionTracker = {};
var timeBin = 0;

var playerSnitchMovements = {};

/*
msg struct:
	key|<data>

	player hits snitch:
		sn|player_name|e:x:y:z|server 

	player login
		li|player_name|server
	player logout
		lo|player_name|server

	^ these two are gonna be for trying to associate accounts

	player position
		pp|player_name|(s/r/l)|e:x:y:z|server
			s - self report
			r - radar report
			l - logout report
*/

redisClient.set("testing1234", "OK");

var validUsername = function(username) {
	// Should I also check if it contains illegal characters?
	if (username.length > 0 && username.length < 17) {
		// checks for illegal minecraft name characters
		// if test returns true, it's got some illegal characters
		return !/[^a-zA-Z0-9_].*/.test(username);
	}
	return false;
}

var validCoordinates = function(serializedCoords) {
	// serialized coords are e:x:y:z
	// e - world id; x, y, z spatial coordss
	return /^(\-?\d):(\-?\d{1,5})\.\d{1,2}:\d{1,2}\.\d{1,2}:(\-?\d{1,5}\.\d{1,2})/.test(serializedCoords);
}

var validSnitchMessage = function(msgParts) {
	// Do the sanity checks 
	return (msgParts.length === 3) && validUsername(msgParts[0]) && validCoordinates(msgParts[1]);
}

var flushDataBeforeTime = function(timestamp) {
	var playerMovementInserts = ["player_snitch_position"];
	for (var username in playerSnitchMovements) {
		for (var timeBin in playerSnitchMovements[username]) {
			// field
			playerMovementInserts.push(username);
			// value
			playerMovementInserts.push(timeBin + "|" + playerSnitchMovements[username][timeBin]);
		}
	}

	var playerSnitchLogouts = ["player_snitch_logout"];
	for (var username in playerSnitchLogouts) {
		for (var timeBin in playerSnitchLogouts[username]) {
			// field
			playerSnitchLogouts.push(username);
			// value
			playerSnitchLogouts.push(timeBin + "|" + playerSnitchLogouts[username][timeBin]);
		}
	}

	if (playerMovementInserts.length > 1) {
		redisClient.hmset(playerMovementInserts, function (err, reply) {
			console.log(reply);
		});
	}

	if (playerSnitchLogouts.length > 1) {
		redisClient.hmset(playerSnitchLogouts, function (err, reply) {

		});
	}
}

util.log("starting server");
var server = dgram.createSocket('udp4', function (msg, rinfo) {
	console.log(msg.toString());
	var msgParts = msg.toString().split('|');
	var msgType = msgParts.shift();

	switch(msgType) {
		// snitch data
		case msgTags.SNITCH_ENTRY:
			if (validSnitchMessage(msgParts)) {
				console.log(msgParts[0] + " entered at " + msgParts[1]);

				if (!playerSnitchMovements[msgParts[0]]) {
					playerSnitchMovements[msgParts[0]] = {};
				}
				playerSnitchMovements[msgParts[0]][timeBin] = msgParts[1];
			} else {
				console.log("invalid snitch message");
			}
			break;
		case msgTags.SNITCH_LOGIN:
			if (validSnitchMessage(msgParts)) {
				console.log(msgParts[0] + " logged in at " + msgParts[1]);
				if (!playerSnitchMovements[msgParts[0]]) {
					playerSnitchMovements[msgParts[0]] = {};
				}
				playerSnitchMovements[msgParts[0]][timeBin] = msgParts[1];
			}
			break;
		case msgTags.SNITCH_LOGOUT:
			if (validSnitchMessage(msgParts)) {
				console.log(msgParts[0] + " logged out at " + msgParts[1]);
				if (!playerSnitchLogouts[msgParts[0]]) {
					playerSnitchLogouts[msgParts[0]] = {};
				}
				playerSnitchLogouts[msgParts[0]][timeBin] = msgParts[1];
			}
			break;

		// log in
		case msgTags.PLAYER_LOGIN:
			if (validUsername(msgParts[0])) {
				// Make sure we can start tracking
				if (!playerSessionTracker[msgParts[0]]) {
					playerSessionTracker[msgParts[0]] = {};
				}
				// Start tracking on first report
				if (!playerSessionTracker[msgParts[0]]['login']) {
					console.log("started tracking session for " + msgParts[0]);
					playerSessionTracker[msgParts[0]]['login'] = timeBin;
				}
			}
			break;
		// log out
		case msgTags.PLAYER_LOGOUT:
			if (validUsername(msgParts[0])) {
				// If we have his login, track his logout
				if (playerSessionTracker[msgParts[0]]) {
					if (playerSessionTracker[msgParts[0]]['login']) {
						if (!playerSessionTracker[msgParts[0]]['logout']) {
							playerSessionTracker[msgParts[0]]['logout'] = timeBin;
						}
					}
				}
			}
			break;
		case msgTags.POSITION_REPORT:
			break;
		default:

			break;
	}
});

server.bind(9002); // it's over 9000

var updateTime = function() {
	timeBin = Math.round((new Date()).getTime()/1000);
	flushDataBeforeTime(timeBin);
	setTimeout(updateTime, 2500);
}
updateTime();

process.on('exit', function() {
	// do anything here I need to do
});
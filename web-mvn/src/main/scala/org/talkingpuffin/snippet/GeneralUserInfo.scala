package org.talkingpuffin.snippet

import scala.collection.JavaConversions._
import java.text.NumberFormat
import org.talkingpuffin.apix.PartitionedTweets
import twitter4j.User
import org.talkingpuffin.apix.RichStatus._
import org.talkingpuffin.user.UserAnalysis

object GeneralUserInfo {
  def create(user: User, screenName: String, pt: PartitionedTweets): List[String] = {
    val ua = UserAnalysis(pt)
    var msgs = List[String]()
    val fmt = NumberFormat.getInstance
    def disp(msg: String) = msgs = msg :: msgs
    disp(user.getName + " (" + user.getScreenName + ")")
    disp(user.getLocation)
    disp(user.getDescription)
    disp("Followers: " + fmt.format(user.getFollowersCount) +
    ", following: " + fmt.format(user.getFriendsCount))
    disp("Tweets analyzed: " + ua.numTweets)
    disp("Range: " + ua.range.getDays + " days")
    disp( "Avg per day: " + fmt.format(ua.avgTweetsPerDay))
    if (ua.numReplies > 0) {
      disp("Avg per day excluding replies: " + fmt.format(ua.avgTweetsPerDayExcludingReplies))
    }
    if (ua.numLinks > 0)
      disp("Links in tweets: " + ua.numLinks + " (" + ua.links.distinct.size + " unique)")
    if (ua.numUsers > 0)
      disp("Users mentioned: " + ua.numUsers + " (" + ua.users.distinct.size + " unique)")
    disp("Word frequencies:")
    for (freq <- ua.buckets.keysIterator.filter(_ > 2).toList.sorted.reverse)
      disp(freq.toString + ": " + ua.buckets.get(freq).get.sorted.mkString(", "))
    msgs.reverse
  }
}
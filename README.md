# TabModifier
A tab manager plugin based on SpongeAPI 7 and LuckPerms
- New Version 1.4.x now requires PlaceholderApi
Commands
- tab reload : reload plugin config
- tab refresh : refresh all players' tablist
- tab setheader : set header
- tab setfooter : set footer
Permissions
- tabmodifier.reload
- tabmodifier.refresh
- tabmodifier.setheader
- tabmodifier.setfooter
FAQ
- How to set prefix/suffix
You should use luckperms to set prefix/suffix value
  - lp user [username] meta setprefix [weight] [prefix]
  - lp user [username] meta setsuffix [weight] [suffix]
  - lp group [groupname] meta setprefix [weight] [prefix]
  - lp group [groupname] meta setsuffix [weight] [suffix]
  - if one player/group has multiple prefix/suffix, tabmodifier will user that has highest weight value
What does InitialValue mean in config
  - tabmodifier has its own priority :
    - user's prefix > group's prefix > InitialValue
    - user's suffix > group's suffix > InitialValue
  - when user's prefix is not found, tabmodifier will try to get user's primary group's prefix, if still not found,
  it will use InitialValue
What does RefreshDelay mean in config
  - remember, player's tablist is managed by Sponge/Minecraft, when new player login,
  Sponge/Minecraft will generate new tablist entry
  - tabmodifier will try to get that entry and update all players' tablist
  - but sometimes, because of server lag, that entry will not be generated instantly
  - RefreshDelay tells tabmodifier to wait a few ticks before get that entry
  - you can simply set RefreshDelay to 0 if you have confidence on your server's performance
Do you support placeholderapi
  - Yeap, you can set placeholder where you want : prefix/suffix/header/footer
  - Theoretically tabmodifier support all placeholders
Do you support multiline header/footer
  - Yeap, use \n
Do you support format code
  - Yeap, use &

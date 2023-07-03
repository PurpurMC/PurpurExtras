Contributing to PurpurExtras
==========================
Purpur is happy you're willing to contribute to our projects. We are usually
very lenient with all submitted PRs, but there are still some guidelines you
can follow to make the approval process go more smoothly.

## Use a Personal Fork and not Organization

Purpur will routinely modify your PR, whether it's a quick rebase or to take care
of any minor nitpicks we might have. Often, it's better for us to solve these
problems for you than make you go back and forth trying to fix it yourself.

Unfortunately, if you use an organization for your PR, it prevents Purpur from
modifying it. This requires us to manually merge your PR, resulting in us
closing the PR instead of marking it as merged.

We much prefer to have PRs show as merged, so please do not use repositories
on organizations for PRs.

See <https://github.com/isaacs/github/issues/1681> for more information on the
issue.

## PR Policy

PurpurExtras uses modules to organize the code for its features. Each module has
to implement PurpurExtrasModule. Adding entries to any registries that are called
from places other than modules package, including registering bukkit event
listeners, will end up with PR not being merged and changes being requested.

We'll accept changes that make sense. You should be able to justify their existence,
along with any maintenance costs that come with them. Remember that these changes
will affect everyone who runs Purpur, not just you and your server.

## Creating a PurpurExtras Module

Creating a module for PurpurExtras is extremely easy! All you have to do is new class
in the package `org.purpurmc.purpurextras.modules.implementation`, write your Listeners, give it an
`@ModuleInfo` annotation, and you're set! PurpurExtras internally handles all module registration
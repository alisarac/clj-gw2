# clj-gw2

A Clojure wrapper for [Guild Wars 2 API](http://wiki.guildwars2.com/wiki/API).

## Usage

Guild Wars 2 API

**/items**
**/items/28445**
**/items/12452**

**/recipes/search?input=46731**
**/event_details?event_id=EED8A79F-B374-4AE6-BA6F-B7B98D9D7142**

in clj-gw2

**(items)**
**(items 28445 12452)** ;; in one call 2 items can be retrieved (2 async http calls)

**(recipes-search-input 46731)**
**(event-details-event-id  "EED8A79F-B374-4AE6-BA6F-B7B98D9D7142")**

## License

Copyright © 2015 Ali Sarac

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

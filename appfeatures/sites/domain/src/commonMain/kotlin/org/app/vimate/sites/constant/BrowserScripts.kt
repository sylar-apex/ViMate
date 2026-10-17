package org.app.vimate.sites.constant

/** Scripts injected into the in-app browser (POC ShortsSites.kt / Grabber.kt), verbatim. */
object BrowserScripts {
    const val GRAB = """
(function(){
  var out = new Set();
  function add(u){ try { if (!u) return; u = new URL(u, location.href).href; if (/^(https?:|magnet:)/.test(u)) out.add(u); } catch(e){} }
  document.querySelectorAll('a[href]').forEach(function(a){ add(a.getAttribute('href')); });
  document.querySelectorAll('img,video,audio,source,track,embed,iframe').forEach(function(e){
    add(e.currentSrc); add(e.getAttribute('src')); add(e.getAttribute('poster')); add(e.getAttribute('data-src'));
    var ss = e.getAttribute('srcset'); if (ss) ss.split(',').forEach(function(p){ add(p.trim().split(' ')[0]); });
  });
  document.querySelectorAll('[style*="url("]').forEach(function(e){
    var m = (e.getAttribute('style')||'').match(/url\(["']?([^"')]+)["']?\)/g); if (m) m.forEach(function(x){ add(x.replace(/url\(["']?|["']?\)/g,'')); });
  });
  document.querySelectorAll('meta[property^="og:"],meta[name^="twitter:"]').forEach(function(m){ var c = m.getAttribute('content'); if (c && /^https?:/.test(c)) add(c); });
  return JSON.stringify(Array.from(out));
})()
"""
    const val SNIFF = """
(function(){
  if (window.__vmsniff) return; window.__vmsniff = true;
  function scan(){ document.querySelectorAll('video,audio,source').forEach(function(e){
    var s = e.currentSrc || e.src; if (s && s.indexOf('http')===0) VMSniff.media(s); }); }
  scan(); new MutationObserver(scan).observe(document.documentElement,{subtree:true,childList:true,attributes:true,attributeFilter:['src']});
  document.addEventListener('play', scan, true);
})();
"""
    const val IG_BUTTONS = """
(function(){
  if (window.__vmig) return; window.__vmig = true;
  var RX = /\/(?:p|reels?|tv)\/(?!audio\/)([A-Za-z0-9_-]{5,})/;
  function code(el){
    // Only links that actually carry a shortcode (profile "/user/reels/" links don't).
    // On the Reels viewer the address bar tracks the reel on screen.
    if (/^\/reels?\/[A-Za-z0-9_-]{5,}/.test(location.pathname) && !el.querySelector('article')) { var lp = location.pathname.match(RX); if (lp && lp[1] !== 'audio') return lp[1]; }
    var links = el.querySelectorAll('a[href]');
    for (var i = 0; i < links.length; i++) { var m = (links[i].getAttribute('href') || '').match(RX); if (m && m[1] !== 'audio') return m[1]; }
    var l = location.pathname.match(RX);
    return l ? l[1] : null;
  }
  function add(){
    var posts = document.querySelectorAll('article');
    if (!posts.length && /\/(p|reels?|tv)\//.test(location.pathname)) posts = [document.querySelector('main') || document.body];
    posts.forEach(function(p){
      var media = p.querySelector('video') || p.querySelector('img[style*="object-fit"]') || p.querySelector('div[role="button"] img');
      if (!media) return;
      var box = media.closest('div[role="presentation"]') || media.parentElement;
      if (!box || box.querySelector('.__vmbtn')) return;
      if (getComputedStyle(box).position === 'static') box.style.position = 'relative';
      var b = document.createElement('div');
      b.className = '__vmbtn';
      b.style.cssText = 'position:absolute;right:0;bottom:56px;width:56px;height:56px;border-radius:50%;background:#F2343B;z-index:9999;display:flex;align-items:center;justify-content:center;box-shadow:0 2px 6px rgba(0,0,0,.3)';
      b.innerHTML = '<svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 17v2a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-2"/><path d="M7 11l5 5l5-5"/><path d="M12 4l0 12"/></svg>';
      b.addEventListener('click', function(e){ e.preventDefault(); e.stopPropagation(); var c = code(p); if (c) VMSniff.igPost(c); }, true);
      box.appendChild(b);
    });
  }
  add(); new MutationObserver(add).observe(document.documentElement, {subtree:true, childList:true});
})();
"""

    /** JS bridge object name the scripts call (`VMSniff.media`, `VMSniff.igPost`). */
    const val BRIDGE = "VMSniff"

    /** "Desktop site" user agent. */
    const val DESKTOP_UA = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0 Safari/537.36"
}

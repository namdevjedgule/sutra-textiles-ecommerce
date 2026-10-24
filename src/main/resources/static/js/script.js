window.Sutra = window.Sutra || {};

window.Sutra.cart = {
  key: 'sutra_cart_count',

  get() {
    return parseInt(localStorage.getItem(this.key) || '0', 10);
  },

  set(n) {
    const count = Math.max(0, Number(n) || 0);
    localStorage.setItem(this.key, String(count));
    document.querySelectorAll('#cartCount').forEach(el => {
      el.textContent = count;
    });
  },

  add(n = 1) {
    this.set(this.get() + n);
  }
};

document.addEventListener('DOMContentLoaded', () => {
  initLayout();
  initPage();
});

function initLayout() {

  const yearEl = document.getElementById('year');

  if (yearEl) {
    yearEl.textContent = new Date().getFullYear();
  }

  const navbarWrap = document.getElementById('navbar');
  const navSpacer = document.getElementById('navSpacer');

  if (navbarWrap) {

    const syncSpacer = () => {
      if (navSpacer) {
        navSpacer.style.height =
          navbarWrap.offsetHeight + 'px';
      }
    };

    const onScroll = () => {
      navbarWrap.classList.toggle(
        'is-scrolled',
        window.scrollY > 40
      );
      syncSpacer();
    };

    onScroll();
    window.addEventListener('scroll', onScroll, {
      passive: true
    });
    window.addEventListener('resize', syncSpacer);
    navbarWrap.addEventListener(
      'transitionend',
      syncSpacer
    );

    if (document.fonts && document.fonts.ready) {
      document.fonts.ready.then(syncSpacer);
    }
  }

  const navToggle = document.getElementById('navToggle');
  const navLinks = document.getElementById('navLinks');
  const navScrim = document.getElementById('navScrim');

  function closeMenu() {
    if (!navToggle || !navLinks || !navScrim) {
      return;
    }

    navToggle.classList.remove('is-open');
    navLinks.classList.remove('is-open');
    navScrim.classList.remove('is-open');
    navToggle.setAttribute('aria-expanded', 'false');
  }

  function toggleMenu() {
    if (!navToggle || !navLinks || !navScrim) {
      return;
    }

    const isOpen = navLinks.classList.toggle('is-open');
    navToggle.classList.toggle('is-open', isOpen);
    navScrim.classList.toggle('is-open', isOpen);
    navToggle.setAttribute(
      'aria-expanded',
      String(isOpen)
    );
  }

  if (navToggle && navLinks && navScrim) {
    navToggle.addEventListener('click', toggleMenu);
    navScrim.addEventListener('click', closeMenu);
    navLinks.querySelectorAll('a').forEach(link => {
      link.addEventListener('click', closeMenu);
    });
    window.addEventListener('resize', () => {
      if (window.innerWidth > 920) {
        closeMenu();
      }
    });
  }

  window.Sutra.cart.set(window.Sutra.cart.get());

  document.querySelectorAll('.add-cart[data-product]')
    .forEach(button => {

      button.addEventListener('click', event => {
        event.preventDefault();
        window.Sutra.cart.add(1);
        const originalText = button.textContent;
        button.textContent = 'Added ✓';
        button.classList.add('is-added');
        button.setAttribute('aria-live', 'polite');

        setTimeout(() => {
          button.textContent = originalText;
          button.classList.remove('is-added');
        }, 1400);
      });
    });
}

function initPage() {
  const slides = document.querySelectorAll('.testimonial-slide');
  const dots = document.querySelectorAll('.testimonial-dot');

  if (slides.length && dots.length) {
    let active = 0;
    let timer = null;

    function showSlide(index) {
      slides[active].classList.remove('is-active');
      dots[active].classList.remove('is-active');
      active = (index + slides.length) % slides.length;
      slides[active].classList.add('is-active');
      dots[active].classList.add('is-active');
    }

    function nextSlide() {
      showSlide(active + 1);
    }

    function stopAuto() {
      if (timer !== null) {
        clearInterval(timer);
        timer = null;
      }
    }

    function startAuto() {
      stopAuto();
      timer = setInterval(nextSlide, 6000);
    }

    dots.forEach((dot, index) => {
      dot.addEventListener('click', () => {
        showSlide(index);
        startAuto();
      });
    });

    const testimonialWrap =
      document.querySelector('.testimonial-wrap');

    if (testimonialWrap) {
      testimonialWrap.addEventListener('mouseenter', stopAuto);
      testimonialWrap.addEventListener('mouseleave', startAuto);
    }

    startAuto();
  }

  const newsletterForm =
    document.getElementById('newsletterForm');
  const newsletterNote =
    document.getElementById('newsletterNote');
  const newsletterEmail =
    document.getElementById('newsletterEmail');

  if (newsletterForm && newsletterEmail && newsletterNote) {
    newsletterForm.addEventListener('submit', event => {
      event.preventDefault();
      const email = newsletterEmail.value.trim();
      const isValid =
        /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

      if (isValid) {
        newsletterNote.textContent =
          'Your email looks valid. Newsletter registration will be available soon.';
        newsletterNote.classList.remove('is-success');
        newsletterNote.classList.add('is-info');

      } else {
        newsletterNote.textContent =
          'Please enter a valid email address.';
        newsletterNote.classList.remove('is-success');
        newsletterNote.classList.remove('is-info');
      }
    });
  }

}

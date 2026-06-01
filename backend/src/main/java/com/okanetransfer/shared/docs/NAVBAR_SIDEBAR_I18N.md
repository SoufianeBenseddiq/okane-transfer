# Shared UI Components — Developer Guide

> Covers **Topbar**, **Sidebar**, and **i18n**. All three are standalone components — import them directly, no module needed.

---

## 1. Topbar (`app-topbar`)

**Location:** `src/app/shared/components/topbar/topbar.component.ts`

### Usage

```html
<app-topbar
  breadcrumb="Administration"
  title="Utilisateurs"
  subtitle="12 comptes au total">

  <!-- Optional: action buttons projected into the right slot -->
  <button class="btn-primary">Créer</button>

</app-topbar>
```

### Inputs

| Input | Type | Description |
|---|---|---|
| `breadcrumb` | `string` | Small label above the title (e.g. section name) |
| `title` | `string` | Main page title, large and bold |
| `subtitle` | `string` | Smaller text below the title |

- **Right slot** (`<ng-content />`): anything placed inside `<app-topbar>` renders to the right of the title, before the bell and language switcher. Use it for CTA buttons.
- **Bell icon**: automatically shows unread notification count for `ROLE_ADMIN` and navigates to `/admin/notifications`. No config needed.
- **Language switcher**: always visible, switches between `fr`, `en`, `ar` instantly.
- **Sidebar toggle button**: built-in, calls `SidebarStateService.toggle()`.

### Import

```typescript
import { TopbarComponent } from '../../shared/components/topbar/topbar.component';

@Component({
  imports: [TopbarComponent],
})
```

### Layout contract

The topbar is `flex-shrink-0` and expects to be a **flex column child** inside the layout. It does not scroll. Place it as the first child, followed by a `flex-1 overflow-y-auto` content div.

```
<div class="flex flex-col flex-1 min-w-0 overflow-hidden">
  <app-topbar />               ← fixed height
  <div class="flex-1 overflow-y-auto p-7">
    <router-outlet />          ← scrollable content
  </div>
</div>
```

---

## 2. Sidebar (`app-sidebar`)

**Location:** `src/app/shared/components/sidebar/sidebar.component.ts`

### Usage

Drop it once in the layout. No inputs — it reads the current user's role automatically.

```html
<div class="flex overflow-hidden" style="height:100vh;">
  <app-sidebar />
  <div class="flex flex-col flex-1 min-w-0 overflow-hidden">
    ...
  </div>
</div>
```

### How nav items are driven

Nav items are defined in `NAV_CONFIG` inside the sidebar component, keyed by role:

```typescript
const NAV_CONFIG: Record<string, NavItem[]> = {
  [RoleUtilisateur.ROLE_ADMIN]: [
    { id: 'dashboard', label: 'nav.dashboard', icon: 'dashboard', route: '/admin/dashboard' },
    { id: 'agences',   label: 'nav.agences',   icon: 'building',  route: '/admin/agences' },
    // ...
  ],
  [RoleUtilisateur.ROLE_AGENT]: [
    { id: 'envoi',  label: 'nav.transfert', icon: 'send',   route: '/agent/envoi' },
    // ...
  ],
};
```

**To add a nav item for a role**, add an entry to the correct array. The sidebar auto-renders it.

| `NavItem` field | Required | Description |
|---|---|---|
| `id` | ✓ | Unique key within the role |
| `label` | ✓ | i18n key (e.g. `'nav.dashboard'`) |
| `icon` | ✓ | Icon name from `IconComponent` (see below) |
| `route` | ✓ | Full Angular route path |
| `badge` | — | Optional red number badge |

**To add a new role**, add a new key to `NAV_CONFIG` and `ROLE_COLORS`:

```typescript
const NAV_CONFIG = {
  [RoleUtilisateur.ROLE_CLIENT]: [
    { id: 'home', label: 'nav.home', icon: 'dashboard', route: '/client/home' },
  ],
};

const ROLE_COLORS = {
  [RoleUtilisateur.ROLE_CLIENT]: '#9D7FFF',
};
```

### Collapse state

`SidebarStateService` persists the collapsed state in `localStorage` under `sidebar_collapsed`. The sidebar toggles between `240px` (expanded) and `64px` (collapsed). No extra config needed.

### Available icons

Icons are SVG paths registered in `IconComponent`. Current set:

`dashboard` · `currency` · `percent` · `building` · `users` · `shield` · `list` · `report` · `send` · `arrowDownLeft` · `wallet` · `history` · `bell` · `logout` · `check` · `plus` · `search` · `chevronRight` · `chevronLeft` · `chevronDown` · `arrowUp` · `arrowDown` · `arrowRight` · `edit` · `trash` · `alert` · `eye` · `eyeOff` · `lock` · `mail` · `phone` · `x` · `sparkle` · `globe` · `menu` · `settings`

To add a new icon, add an entry to the `P` map in `icon.component.ts`:

```typescript
const P: Record<string, string> = {
  myIcon: '<path d="..."/>',
};
```

Then use it anywhere: `<app-icon name="myIcon" [size]="18" />`.

---

## 3. i18n

**Library:** `@ngx-translate/core`  
**Translation files:** `src/assets/i18n/fr.json` · `en.json` · `ar.json`

### Add a translation key

Open all three JSON files and add the same key path. **All three must be in sync.**

```jsonc
// fr.json
"myPage": {
  "title": "Ma page",
  "createBtn": "Créer"
}

// en.json
"myPage": {
  "title": "My page",
  "createBtn": "Create"
}

// ar.json
"myPage": {
  "title": "صفحتي",
  "createBtn": "إنشاء"
}
```

### Use in a template

Import `TranslatePipe` (preferred) or `TranslateModule` in the component:

```typescript
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  imports: [TranslatePipe],
})
```

Then in the template:

```html
<!-- Simple key -->
<h1>{{ 'myPage.title' | translate }}</h1>

<!-- With dynamic params -->
<p>{{ 'users.subtitle' | translate:{ count: users.length } }}</p>

<!-- On an attribute -->
<input [placeholder]="'common.search' | translate">

<!-- Ternary -->
<span>{{ isActive ? ('common.active' | translate) : ('common.inactive' | translate) }}</span>
```

### Use in TypeScript

```typescript
import { TranslateService } from '@ngx-translate/core';

constructor(private translate: TranslateService) {}

// Synchronous (returns current value or key if missing)
const label = this.translate.instant('myPage.title');

// Reactive (emits when language changes)
this.translate.get('myPage.title').subscribe(val => ...);
```

### Key naming conventions

| Prefix | Use for |
|---|---|
| `nav.*` | Sidebar navigation labels |
| `common.*` | Shared actions: save, cancel, delete, loading… |
| `admin.*` | Admin-only pages |
| `manager.*` | Manager-only pages |
| `agent.*` (and `envoi.*`, `payment.*`, `caisse.*`, `historique.*`, `cloture.*`) | Agent pages |
| `pages.*` | Generic page titles |

### Switch language programmatically

```typescript
import { LanguageService } from '../core/services/language.service';

constructor(private lang: LanguageService) {}

this.lang.use('ar'); // 'fr' | 'en' | 'ar'
```

The topbar language switcher does this automatically — no manual wiring needed.

---

## Quick-start checklist for a new page

```
1. Create component (standalone: true)
2. Import TopbarComponent + TranslatePipe
3. Add <app-topbar breadcrumb="..." title="..."> to the template
4. Add the route to the feature's routes file
5. Add a nav item to NAV_CONFIG in sidebar.component.ts
6. Add the nav.* i18n key to all 3 JSON files
7. Add page-level i18n keys to all 3 JSON files
```

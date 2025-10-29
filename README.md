<p align="center">
  <img src="https://cdn.modrinth.com/data/cached_images/bf60f6ce377bfad820b809e192d155d6e161199b.png" alt="CryptenAuth Banner">
</p>

# CryptenAuth

A robust and flexible authentication solution for your Minecraft server. CryptenAuth provides essential player security with multiple data storage options and a streamlined login experience.

## Features

*   **Secure Authentication:** Players can register, log in, and unregister their accounts.
*   **Multiple Storage Options:** Supports YAML, SQLite, and MySQL for data persistence, giving you the flexibility to choose what fits your server best.
*   **Session Management:** Keeps players logged in for a configurable duration, enhancing convenience without compromising security.
*   **Login Timeout:** Prevents unauthenticated players from interacting with the server until they log in or register.
*   **Easy Configuration:** Simple `config.yml` for quick setup and customization.

## Commands

*   `/register <password> <confirm_password>`: Create your account.
*   `/login <password>`: Access your account.
*   `/unregister <password>`: Remove your account.
*   `/cryptenauthreload` or `/cpreload`: Reload the plugin configuration (admin only).

## Installation

1.  Download the latest version of CryptenAuth.
2.  Place the `CryptenAuth-0.1.0.jar` file into your server's `plugins` folder.
3.  Start or restart your server.
4.  Configure `plugins/CryptenAuth/config.yml` to your preferences.

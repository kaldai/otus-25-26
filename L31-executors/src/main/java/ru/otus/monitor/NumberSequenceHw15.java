package ru.otus.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumberSequenceHw15 {
  private static final Logger logger = LoggerFactory.getLogger(NumberSequenceHw15.class);

  private int currentNumber = 1;
  private boolean ascending = true;
  private int nextToPrint = 1; // 1 или 2 — кто должен напечатать текущее число

  public void step(int threadId) {
    while (!Thread.currentThread().isInterrupted()) {
      synchronized (this) {
        try {
          // Ждать, пока не наступит наш ход для текущего числа
          while (nextToPrint != threadId) {
            this.wait();
          }

          // Печатаем текущее число
          logger.info("Поток {}: {}", threadId, currentNumber);

          // Если это второй поток — переходим к следующему числу/направлению
          if (threadId == 2) {
            updateState();
          }

          // Передаём ход другому потоку для того же числа
          nextToPrint = (nextToPrint == 1) ? 2 : 1;

          // Оповещаем все потоки
          this.notifyAll();

        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          return;
        }
      }
    }
  }

  private void updateState() {
    if (ascending) {
      currentNumber++;
      if (currentNumber == 10) {
        ascending = false;
      }
    } else {
      currentNumber--;
      if (currentNumber == 1) {
        ascending = true;
      }
    }
  }

  public static void main(String[] args) {
    NumberSequenceHw15 sequence = new NumberSequenceHw15();

    Thread thread1 = new Thread(() -> sequence.step(1));
    Thread thread2 = new Thread(() -> sequence.step(2));

    // Поток 1 начинает первым
    thread1.start();
    thread2.start();
  }
}
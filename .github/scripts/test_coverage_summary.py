import contextlib
import io
import os
import tempfile
import unittest

import coverage_summary

EXPECTED = """<!-- kover-coverage -->
### Unit test coverage

| Module | Line coverage | Covered / total lines |
|---|---:|---:|
| app | 12.5% | 10 / 80 |
| core | 0.0% | 0 / 40 |
| **Total** | **8.3%** | **10 / 120** |

Full HTML and XML reports are in the `coverage-report` artifact of this run.
"""


def write_xml(directory, name, body):
    path = os.path.join(directory, name)
    with open(path, "w") as f:
        f.write('<?xml version="1.0" encoding="UTF-8"?><report name="x">' + body + "</report>")
    return path


class CoverageSummaryTest(unittest.TestCase):
    def setUp(self):
        self._tmp = tempfile.TemporaryDirectory()
        self.addCleanup(self._tmp.cleanup)
        self.dir = self._tmp.name

    def test_read_line_counts_returns_report_level_counter(self):
        path = write_xml(
            self.dir,
            "a.xml",
            '<package name="p"><counter type="LINE" missed="1" covered="1"/></package>'
            '<counter type="BRANCH" missed="5" covered="5"/>'
            '<counter type="LINE" missed="70" covered="10"/>',
        )
        self.assertEqual(coverage_summary.read_line_counts(path), (10, 70))

    def test_read_line_counts_without_counter_returns_zero(self):
        path = write_xml(self.dir, "b.xml", "")
        self.assertEqual(coverage_summary.read_line_counts(path), (0, 0))

    def test_render_matches_expected_markdown(self):
        out = coverage_summary.render((10, 110), [("app", 10, 70), ("core", 0, 40)])
        self.assertEqual(out, EXPECTED)

    def test_render_module_without_tests_shows_zero_percent(self):
        out = coverage_summary.render((0, 40), [("core", 0, 40)])
        self.assertIn("| core | 0.0% | 0 / 40 |", out)

    def test_render_empty_module_shows_na(self):
        out = coverage_summary.render((0, 0), [("push", 0, 0)])
        self.assertIn("| push | n/a | 0 / 0 |", out)

    def test_main_missing_report_exits_nonzero(self):
        total = write_xml(self.dir, "t.xml", '<counter type="LINE" missed="1" covered="1"/>')
        err = io.StringIO()
        with contextlib.redirect_stderr(err):
            code = coverage_summary.main(["--total", total, "--module", "app=/nope.xml"])
        self.assertEqual(code, 1)
        self.assertIn("/nope.xml", err.getvalue())

    def test_main_prints_markdown(self):
        total = write_xml(self.dir, "t.xml", '<counter type="LINE" missed="1" covered="1"/>')
        mod = write_xml(self.dir, "m.xml", '<counter type="LINE" missed="1" covered="1"/>')
        out = io.StringIO()
        with contextlib.redirect_stdout(out):
            code = coverage_summary.main(["--total", total, "--module", "app=" + mod])
        self.assertEqual(code, 0)
        self.assertTrue(out.getvalue().startswith("<!-- kover-coverage -->"))

    def test_render_links_run_when_url_given(self):
        out = coverage_summary.render((1, 1), [], run_url="https://example.test/run/1")
        self.assertIn(
            "Full HTML and XML reports are in the `coverage-report` artifact of "
            "[this run](https://example.test/run/1).\n",
            out,
        )

    def test_main_passes_run_url(self):
        total = write_xml(self.dir, "t.xml", '<counter type="LINE" missed="1" covered="1"/>')
        out = io.StringIO()
        with contextlib.redirect_stdout(out):
            code = coverage_summary.main(["--total", total, "--run-url", "https://example.test/run/1"])
        self.assertEqual(code, 0)
        self.assertIn("[this run](https://example.test/run/1)", out.getvalue())


if __name__ == "__main__":
    unittest.main()
